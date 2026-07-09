package com.financeos.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.financeos.app.data.FinanceDatabase
// Make sure this matches wherever you put your SmsParser!
import com.financeos.app.utils.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    // Create a background worker that won't freeze the app
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        // Did we actually receive a text message?
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            // Extract the raw text data from the Android intent
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (sms in messages) {
                val sender = sms.displayOriginatingAddress ?: ""
                val body = sms.displayMessageBody ?: ""

                // 1. Pass the raw text to our Regex Brain
                val parsedTransaction = SmsParser.parseMessage(sender, body)

                // 2. If the Brain successfully extracted a transaction, save it!
                if (parsedTransaction != null) {

                    // Tells Android: "Hold the door open for a millisecond, I'm doing background work!"
                    val pendingResult = goAsync()

                    scope.launch {
                        try {
                            // Wake up the database and insert the new receipt
                            val db = FinanceDatabase.getDatabase(context)
                            db.transactionDao().insertTransaction(parsedTransaction)
                        } finally {
                            // Tells Android: "I'm done, you can go back to sleep now."
                            pendingResult.finish()
                        }
                    }
                }
            }
        }
    }
}