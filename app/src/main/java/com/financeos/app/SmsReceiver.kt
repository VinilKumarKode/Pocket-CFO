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
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            val fullMessage = StringBuilder()
            var sender = ""

            for (sms in messages) {
                sender = sms.originatingAddress ?: ""
                fullMessage.append(sms.messageBody)
            }

            // REMOVED THE STRICT FILTER FOR TESTING!
            // It will now process texts from your personal phone number.
            parseAndSaveSms(context, sender, fullMessage.toString())
        }
    }

    private fun parseAndSaveSms(context: Context, sender: String, body: String) {
        val lowerBody = body.lowercase()
        if (!lowerBody.contains("debited") && !lowerBody.contains("spent") && !lowerBody.contains("sent")) {
            return
        }

        var amount = 0.0
        val amountPattern = Pattern.compile("(?i)(?:rs\\.?|inr)\\s*([0-9,]+\\.?[0-9]*)")
        val amountMatcher = amountPattern.matcher(body)
        if (amountMatcher.find()) {
            val amountStr = amountMatcher.group(1)?.replace(",", "")
            amount = amountStr?.toDoubleOrNull() ?: 0.0
        }

        var paymentMethod = "Unknown"
        val accountPattern = Pattern.compile("(?i)(?:a/c|ac|card|ending)[\\s]*[Xx\\*-]*([0-9]{4})")
        val accountMatcher = accountPattern.matcher(body)
        if (accountMatcher.find()) {
            paymentMethod = "Card/Ac *" + accountMatcher.group(1)
        }

        var merchant = "Unknown Merchant"
        val merchantPattern = Pattern.compile("(?i)(?:at|to)\\s+([A-Za-z0-9\\s\\*@]+?)(?:\\s+on|\\.)")
        val merchantMatcher = merchantPattern.matcher(body)
        if (merchantMatcher.find()) {
            merchant = merchantMatcher.group(1)?.trim() ?: "Unknown Merchant"
        }

        if (amount > 0) {
            val smartCategory = CategoryEngine.categorizeMerchant(merchant)
            val calculatedRewards = YieldEngine.calculateRewardValue(amount, smartCategory, paymentMethod)

            val transaction = Transaction(
                amount = amount,
                type = "Debit",
                category = smartCategory,
                date = System.currentTimeMillis(),
                paymentMethod = paymentMethod,
                rewardPointsEarned = calculatedRewards,
                description = merchant,
                isReconciled = false
            )

            CoroutineScope(Dispatchers.IO).launch {
                val db = FinanceDatabase.getDatabase(context)
                db.transactionDao().insertTransaction(transaction)
                Log.d("PocketCFO", "Transaction Saved: ₹$amount at $merchant using $paymentMethod")
            }
        }
    }
}