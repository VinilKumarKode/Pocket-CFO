package com.financeos.app.utils

import com.financeos.app.data.Transaction

object SmsParser {

    fun parseMessage(sender: String, messageBody: String): Transaction? {
        val body = messageBody.lowercase()

        // 1. Is it actually money leaving your account?
        if (!body.contains("debited") && !body.contains("spent") && !body.contains("paid")) {
            return null
        }

        // 2. Extract the Amount (Handles commas like Rs. 7,300.50)
        val amountRegex = Regex("(?i)(?:rs\\.?|inr)\\s*([\\d,]+(?:\\.\\d+)?)")
        val amountMatch = amountRegex.find(messageBody) ?: return null
        val rawAmount = amountMatch.groupValues[1].replace(",", "")
        val amount = rawAmount.toDoubleOrNull() ?: return null

        // 3. Identify the Bank from the Sender ID (e.g., "AD-HDFCBK" -> "HDFC")
        val bankName = when {
            sender.contains("HDFC", ignoreCase = true) -> "HDFC Bank"
            sender.contains("SBI", ignoreCase = true) -> "SBI"
            sender.contains("ICICI", ignoreCase = true) -> "ICICI Bank"
            sender.contains("AXIS", ignoreCase = true) -> "Axis Bank"
            sender.contains("KOTAK", ignoreCase = true) -> "Kotak Bank"
            sender.contains("IDFC", ignoreCase = true) -> "IDFC First"
            sender.contains("BOI", ignoreCase = true) -> "Bank of India"
            else -> sender.replace(Regex("[^A-Za-z]"), "").take(6) // Fallback
        }

        // 4. Extract Card or Account Ending Digits (e.g., "a/c ending in 1234" or "card **3456")
        val accountRegex = Regex("(?i)(?:a/c|acct|card|no\\.?)\\s*[*x\\-]*(\\d{3,4})")
        val accountMatch = accountRegex.find(messageBody)
        val accountEnd = accountMatch?.groupValues?.get(1) ?: "Unknown"

        // Combine them for a beautiful label: "HDFC Bank *1234"
        val paymentMethod = "$bankName *$accountEnd"

        // 5. Extract the Merchant (Usually follows words like "at", "to", or "info")
        val merchantRegex = Regex("(?i)(?:at|to|info)\\s+([A-Za-z0-9\\s&]+?)(?:on|ref|val|\\.)")
        val merchantMatch = merchantRegex.find(messageBody)
        val merchant = merchantMatch?.groupValues?.get(1)?.trim()?.take(20) ?: "Unknown Merchant"

        // 6. Ask our Dictionary what category this merchant belongs to
        val category = CategoryEngine.getCategoryForMerchant(merchant)

        return Transaction(
            amount = amount,
            type = "EXPENSE",
            category = category,
            date = System.currentTimeMillis(), // The Scraper will overwrite this with the real historical date
            paymentMethod = paymentMethod,
            description = merchant,
            isReconciled = false,
            sender = sender,
            rawMessage = messageBody
        )
    }
}