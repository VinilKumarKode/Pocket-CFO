package com.financeos.app.utils

import com.financeos.app.data.Transaction
import com.financeos.app.data.FinancialEntity
import com.financeos.app.data.UpcomingLiability

object SmsParser {

    // --- BRAIN 1: The Dual Transaction Parser (Expenses & Income/Interest) ---
    fun parseMessage(sender: String, messageBody: String): Transaction? {
        val body = messageBody.lowercase()

        // 1. Identify transaction direction
        val isExpense = body.contains("debited") || body.contains("spent") || body.contains("paid") || body.contains("withdrawn")
        val isIncome = body.contains("credited") || body.contains("received") || body.contains("deposited") || body.contains("interest paid")

        if (!isExpense && !isIncome) {
            return null
        }

        // 2. Safe Amount Extraction
        val amountRegex = Regex("(?i)(?:rs\\.?|inr|₹)\\s*([\\d,]+(?:\\.\\d+)?)")
        val amountMatch = amountRegex.find(messageBody) ?: return null
        val rawAmount = amountMatch.groupValues[1].replace(",", "")
        val amount = rawAmount.toDoubleOrNull() ?: return null

        val bankName = extractBankName(sender)

        // 3. Extract Account/Card Details
        val accountRegex = Regex("(?i)(?:a/c|acct|card|no\\.?)\\s*[*x\\-]*(\\d{3,4})")
        val accountMatch = accountRegex.find(messageBody)
        val accountEnd = accountMatch?.groupValues?.get(1) ?: "Unknown"
        val paymentMethod = "$bankName *$accountEnd"

        // 4. Determine categorization and details based on direction of cash flow
        val type: String
        val category: String
        val description: String

        if (isIncome) {
            type = "INCOME"
            category = if (body.contains("interest") || body.contains("dividend")) {
                "Investments & Interest"
            } else if (body.contains("salary")) {
                "Salary"
            } else {
                "Deposits & Transfers"
            }

            description = if (body.contains("interest")) {
                "Automated Interest Credit"
            } else {
                val remitterRegex = Regex("(?i)(?:by|from)\\s+([A-Za-z0-9\\s&]+?)(?:on|ref|val|\\.)")
                val remitterMatch = remitterRegex.find(messageBody)
                remitterMatch?.groupValues?.get(1)?.trim()?.take(20) ?: "Incoming Transfer"
            }
        } else {
            type = "EXPENSE"
            val merchantRegex = Regex("(?i)(?:at|to|info)\\s+([A-Za-z0-9\\s&]+?)(?:on|ref|val|\\.)")
            val merchantMatch = merchantRegex.find(messageBody)
            val merchant = merchantMatch?.groupValues?.get(1)?.trim()?.take(20) ?: "Unknown Merchant"

            category = CategoryEngine.getCategoryForMerchant(merchant)
            description = merchant
        }

        return Transaction(
            amount = amount,
            type = type,
            category = category,
            date = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            description = description,
            isReconciled = false,
            sender = sender,
            rawMessage = messageBody
        )
    }

    // --- BRAIN 2: The Asset Discovery Engine ---
    fun parseFinancialEntity(sender: String, messageBody: String): FinancialEntity? {
        val body = messageBody.lowercase()

        if (!body.contains("bal") && !body.contains("limit") && !body.contains("available")) {
            return null
        }

        val bankName = extractBankName(sender)

        val accountRegex = Regex("(?i)(?:a/c|acct|card|no\\.?)\\s*[*x\\-]*(\\d{3,4})")
        val accountMatch = accountRegex.find(messageBody)
        val accountEnd = accountMatch?.groupValues?.get(1) ?: return null

        val balRegex = Regex("(?i)(?:bal|balance|available|avl)[^\\d]*?(?:rs\\.?|inr)\\s*([\\d,]+(?:\\.\\d+)?)")
        val balMatch = balRegex.find(messageBody)
        val rawBal = balMatch?.groupValues?.get(1)?.replace(",", "")
        val balance = rawBal?.toDoubleOrNull() ?: 0.0

        val limitRegex = Regex("(?i)(?:limit)[^\\d]*?(?:rs\\.?|inr)\\s*([\\d,]+(?:\\.\\d+)?)")
        val limitMatch = limitRegex.find(messageBody)
        val rawLimit = limitMatch?.groupValues?.get(1)?.replace(",", "")
        val limit = rawLimit?.toDoubleOrNull()

        val type = if (body.contains("card") || limit != null) "CREDIT_CARD" else "BANK_ACCOUNT"

        if (balMatch == null && limitMatch == null) return null

        return FinancialEntity(
            name = bankName,
            type = type,
            lastFourDigits = accountEnd,
            balance = balance,
            creditLimit = limit
        )
    }

    // --- BRAIN 3: The Timeline Engine (Upcoming Liabilities) ---
    fun parseUpcomingLiability(sender: String, messageBody: String): UpcomingLiability? {
        val body = messageBody.lowercase()

        if (!body.contains("due") && !body.contains("emi") && !body.contains("statement")) {
            return null
        }
        if (body.contains("paid") || body.contains("received") || body.contains("thank you") || body.contains("min due")) {
            return null
        }

        val amountRegex = Regex("(?i)(?:rs\\.?|inr|amount|due)\\s*[:\\-]?\\s*([\\d,]+(?:\\.\\d+)?)")
        val amountMatch = amountRegex.find(messageBody) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null

        val bankName = extractBankName(sender)

        val type = when {
            body.contains("emi") -> "EMI"
            body.contains("card") || body.contains("statement") -> "CREDIT CARD"
            body.contains("electricity") || body.contains("bill") -> "UTILITY BILL"
            else -> "BILL"
        }

        val dateRegex = Regex("(?i)(?:by|on|before|date)\\s*[:\\-]?\\s*(\\d{1,2}[-/\\s][a-zA-Z]{3,}|\\d{1,2}[-/]\\d{1,2})")
        val dateMatch = dateRegex.find(messageBody)
        val extractedDateStr = dateMatch?.groupValues?.get(1)?.trim()

        val title = if (extractedDateStr != null) {
            "$bankName $type (Due: $extractedDateStr)"
        } else {
            "$bankName $type"
        }

        return UpcomingLiability(
            title = title,
            amountDue = amount,
            dueDate = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000),
            type = type,
            isPaid = false,
            rawMessage = messageBody
        )
    }

    // --- BRAIN 4: The Notification Interceptor (UPI & Apps) ---
    fun parseUpiNotification(appName: String, title: String, text: String): Transaction? {
        val fullText = "$title $text".lowercase()

        if (!fullText.contains("paid") && !fullText.contains("sent")) {
            return null
        }

        val amountRegex = Regex("(?i)(?:₹|rs\\.?|inr)\\s*([\\d,]+(?:\\.\\d+)?)")
        val amountMatch = amountRegex.find(fullText) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null

        val merchantRegex = Regex("(?i)(?:to)\\s+([A-Za-z0-9\\s&]+)")
        val merchantMatch = merchantRegex.find(fullText)
        val merchant = merchantMatch?.groupValues?.get(1)?.trim()?.take(20) ?: "UPI Payment"

        return Transaction(
            amount = amount,
            type = "EXPENSE",
            category = CategoryEngine.getCategoryForMerchant(merchant),
            date = System.currentTimeMillis(),
            paymentMethod = appName,
            description = merchant,
            isReconciled = false,
            sender = appName,
            rawMessage = fullText
        )
    }

    // --- HELPER FUNCTION (Fully Exhaustive 'when' block) ---
    private fun extractBankName(sender: String): String {
        return when {
            sender.contains("HDFC", ignoreCase = true) -> "HDFC Bank"
            sender.contains("SBI", ignoreCase = true) -> "SBI"
            sender.contains("ICICI", ignoreCase = true) -> "ICICI Bank"
            sender.contains("AXIS", ignoreCase = true) -> "Axis Bank"
            sender.contains("KOTAK", ignoreCase = true) -> "Kotak Bank"
            sender.contains("IDFC", ignoreCase = true) -> "IDFC First"
            sender.contains("BOI", ignoreCase = true) -> "Bank of India"
            else -> {
                val cleanSender = sender.replace(Regex("[^A-Za-z]"), "").take(6)
                cleanSender.ifEmpty { "Other" }
            }
        }
    }
}