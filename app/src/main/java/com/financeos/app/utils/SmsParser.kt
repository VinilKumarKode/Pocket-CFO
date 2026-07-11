package com.financeos.app.utils

import com.financeos.app.data.Transaction
import com.financeos.app.data.FinancialEntity

object SmsParser {

    // --- BRAIN 1: The Transaction Parser (From Phase 3) ---
    fun parseMessage(sender: String, messageBody: String): Transaction? {
        val body = messageBody.lowercase()

        if (!body.contains("debited") && !body.contains("spent") && !body.contains("paid")) {
            return null
        }

        val amountRegex = Regex("(?i)(?:rs\\.?|inr)\\s*([\\d,]+(?:\\.\\d+)?)")
        val amountMatch = amountRegex.find(messageBody) ?: return null
        val rawAmount = amountMatch.groupValues[1].replace(",", "")
        val amount = rawAmount.toDoubleOrNull() ?: return null

        val bankName = extractBankName(sender)

        val accountRegex = Regex("(?i)(?:a/c|acct|card|no\\.?)\\s*[*x\\-]*(\\d{3,4})")
        val accountMatch = accountRegex.find(messageBody)
        val accountEnd = accountMatch?.groupValues?.get(1) ?: "Unknown"

        val paymentMethod = "$bankName *$accountEnd"

        val merchantRegex = Regex("(?i)(?:at|to|info)\\s+([A-Za-z0-9\\s&]+?)(?:on|ref|val|\\.)")
        val merchantMatch = merchantRegex.find(messageBody)
        val merchant = merchantMatch?.groupValues?.get(1)?.trim()?.take(20) ?: "Unknown Merchant"

        val category = CategoryEngine.getCategoryForMerchant(merchant)

        return Transaction(
            amount = amount,
            type = "EXPENSE",
            category = category,
            date = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            description = merchant,
            isReconciled = false,
            sender = sender,
            rawMessage = messageBody
        )
    }

    // --- BRAIN 2: The New Asset Discovery Engine (Phase 6) ---
    fun parseFinancialEntity(sender: String, messageBody: String): FinancialEntity? {
        val body = messageBody.lowercase()

        // 1. Check if the message contains balance or limit keywords
        if (!body.contains("bal") && !body.contains("limit") && !body.contains("available")) {
            return null
        }

        val bankName = extractBankName(sender)

        // 2. We MUST have an account identifier to create a profile
        val accountRegex = Regex("(?i)(?:a/c|acct|card|no\\.?)\\s*[*x\\-]*(\\d{3,4})")
        val accountMatch = accountRegex.find(messageBody)
        val accountEnd = accountMatch?.groupValues?.get(1) ?: return null

        // 3. Extract the Available Balance
        val balRegex = Regex("(?i)(?:bal|balance|available|avl)[^\\d]*?(?:rs\\.?|inr)\\s*([\\d,]+(?:\\.\\d+)?)")
        val balMatch = balRegex.find(messageBody)
        val rawBal = balMatch?.groupValues?.get(1)?.replace(",", "")
        val balance = rawBal?.toDoubleOrNull() ?: 0.0

        // 4. Extract Credit Limit (If it's a credit card)
        val limitRegex = Regex("(?i)(?:limit)[^\\d]*?(?:rs\\.?|inr)\\s*([\\d,]+(?:\\.\\d+)?)")
        val limitMatch = limitRegex.find(messageBody)
        val rawLimit = limitMatch?.groupValues?.get(1)?.replace(",", "")
        val limit = rawLimit?.toDoubleOrNull()

        // 5. Determine if it's a Bank Account or Credit Card
        val type = if (body.contains("card") || limit != null) "CREDIT_CARD" else "BANK_ACCOUNT"

        // If we found neither a balance nor a limit, abort.
        if (balMatch == null && limitMatch == null) return null

        return FinancialEntity(
            name = bankName,
            type = type,
            lastFourDigits = accountEnd,
            balance = balance,
            creditLimit = limit
        )
    }

    // A small helper to keep bank logic consistent across both brains
    private fun extractBankName(sender: String): String {
        return when {
            sender.contains("HDFC", ignoreCase = true) -> "HDFC Bank"
            sender.contains("SBI", ignoreCase = true) -> "SBI"
            sender.contains("ICICI", ignoreCase = true) -> "ICICI Bank"
            sender.contains("AXIS", ignoreCase = true) -> "Axis Bank"
            sender.contains("KOTAK", ignoreCase = true) -> "Kotak Bank"
            sender.contains("IDFC", ignoreCase = true) -> "IDFC First"
            sender.contains("BOI", ignoreCase = true) -> "Bank of India"
            else -> sender.replace(Regex("[^A-Za-z]"), "").take(6)
        }
    }
}