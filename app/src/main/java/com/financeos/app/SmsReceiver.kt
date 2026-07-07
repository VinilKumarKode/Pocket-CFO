package com.financeos.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.financeos.app.data.FinanceDatabase
import com.financeos.app.data.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Ensure the broadcast is actually an SMS
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            // Reconstruct the full message (long bank SMS are sometimes split into parts)
            val fullMessage = StringBuilder()
            var sender = ""

            for (sms in messages) {
                sender = sms.originatingAddress ?: ""
                fullMessage.append(sms.messageBody)
            }

            // CFO Logic: Only parse if the sender looks like an institutional ID (e.g., AD-SBICRD, HDFCBK)
            if (sender.matches(Regex(".*[A-Za-z]+.*")) && sender.length >= 6) {
                parseAndSaveSms(context, sender, fullMessage.toString())
            }
        }
    }

    private fun parseAndSaveSms(context: Context, sender: String, body: String) {
        // 1. Filter: Ensure this is a spend/debit transaction
        val lowerBody = body.lowercase()
        if (!lowerBody.contains("debited") && !lowerBody.contains("spent") && !lowerBody.contains("sent")) {
            return // Ignore marketing messages, OTPs, or credits for now
        }

        // 2. Extract Amount (Looks for Rs., INR followed by numbers and decimals)
        var amount = 0.0
        val amountPattern = Pattern.compile("(?i)(?:rs\\.?|inr)\\s*([0-9,]+\\.?[0-9]*)")
        val amountMatcher = amountPattern.matcher(body)
        if (amountMatcher.find()) {
            val amountStr = amountMatcher.group(1)?.replace(",", "") // Remove commas for double parsing
            amount = amountStr?.toDoubleOrNull() ?: 0.0
        }

        // 3. Extract Card/Account Last 4 Digits (Looks for A/c, Card, ending, followed by X or *)
        var paymentMethod = "Unknown"
        val accountPattern = Pattern.compile("(?i)(?:a/c|ac|card|ending)[\\s]*[Xx\\*-]*([0-9]{4})")
        val accountMatcher = accountPattern.matcher(body)
        if (accountMatcher.find()) {
            paymentMethod = "Card/Ac *" + accountMatcher.group(1)
        }

        // 4. Extract Merchant Name (Looks for text specifically after 'at' or 'to')
        var merchant = "Unknown Merchant"
        val merchantPattern = Pattern.compile("(?i)(?:at|to)\\s+([A-Za-z0-9\\s\\*@]+?)(?:\\s+on|\\.)")
        val merchantMatcher = merchantPattern.matcher(body)
        if (merchantMatcher.find()) {
            merchant = merchantMatcher.group(1)?.trim() ?: "Unknown Merchant"
        }

// 5. Save to Database: If a valid amount was found, officially log it.
        if (amount > 0) {

            val smartCategory = CategoryEngine.categorizeMerchant(merchant)

            // NEW: The CFO calculates exactly how much you earned on this swipe
            val calculatedRewards = YieldEngine.calculateRewardValue(amount, smartCategory, paymentMethod)

            val transaction = Transaction(
                amount = amount,
                type = "Debit",
                category = smartCategory,
                date = System.currentTimeMillis(),
                paymentMethod = paymentMethod,
                rewardPointsEarned = calculatedRewards, // <--- This line is updated!
                description = merchant,
                isReconciled = false
            )
            // ... (saving logic remains the same)
            // Save to local Room Database using a Background Coroutine so the phone UI doesn't freeze
            CoroutineScope(Dispatchers.IO).launch {
                val db = FinanceDatabase.getDatabase(context)
                db.transactionDao().insertTransaction(transaction)

                // Print a secret message to your Android Studio logcat so you know it worked!
                Log.d("PocketCFO", "Transaction Saved: ₹$amount at $merchant using $paymentMethod")
            }
        }
    }
}