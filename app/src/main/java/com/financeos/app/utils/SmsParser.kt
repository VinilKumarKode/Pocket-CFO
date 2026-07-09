package com.financeos.app.utils // Update this if you put it in a different folder

import com.financeos.app.data.Transaction

object SmsParser {

    fun parseMessage(sender: String, messageBody: String): Transaction? {
        // 1. Ignore personal texts (OTP, Mom, Friends). Bank senders usually have a '-' like "AD-HDFCBK"
        if (!sender.contains("-") && sender.length < 6) return null

        val lowerCaseMsg = messageBody.lowercase()

        // 2. Identify if it's a debit (expense)
        val isDebit = lowerCaseMsg.contains("debited") || lowerCaseMsg.contains("spent") || lowerCaseMsg.contains("deducted")
        if (!isDebit) return null // For now, we are only tracking expenses

        // 3. Extract the Amount using Regex
        // This looks for "rs.", "rs", or "inr" followed by spaces and numbers/decimals
        val amountRegex = Regex("(?i)(?:rs\\.?|inr)\\s*([0-9,]+\\.?[0-9]*)")
        val amountMatch = amountRegex.find(messageBody)
        val amountString = amountMatch?.groups?.get(1)?.value?.replace(",", "") ?: "0.0"
        val amount = amountString.toDoubleOrNull() ?: 0.0

        if (amount <= 0.0) return null // If we couldn't find an amount, discard it

        // 4. Extract the Merchant (usually comes after "at" or "info:")
        val merchantRegex = Regex("(?i)(?:at|info:)\\s+([A-Za-z0-9\\s]+?)(?:on|\\.)")
        val merchantMatch = merchantRegex.find(messageBody)
        val merchant = merchantMatch?.groups?.get(1)?.value?.trim() ?: "Unknown Merchant"

        // 5. Extract the Card/Account Number (usually comes after "a/c", "acct", or "ending")
        val accountRegex = Regex("(?i)(?:a/c|acct|ending|card)\\s*\\*?([0-9]{4})")
        val accountMatch = accountRegex.find(messageBody)
        val paymentMethod = if (accountMatch != null) {
            "Account *${accountMatch.groups[1]?.value}"
        } else {
            "Unknown Account"
        }

        // 6. Build and return the Transaction!
        return Transaction(
            amount = amount,
            type = "EXPENSE",
            category = "smartcategory", // Fixed
            date = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            description = merchant,
            isReconciled = false,
            sender = sender,
            rawMessage = messageBody
        )
    }
}