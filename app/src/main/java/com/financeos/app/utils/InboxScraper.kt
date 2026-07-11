package com.financeos.app.utils

import android.content.Context
import android.net.Uri
import com.financeos.app.data.FinanceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object InboxScraper {

    suspend fun syncHistoricalData(context: Context): Int = withContext(Dispatchers.IO) {
        val db = FinanceDatabase.getDatabase(context)
        var transactionsFound = 0

        // 1. Give the app a memory of the last time it synced
        val prefs = context.getSharedPreferences("PocketCFO_Prefs", Context.MODE_PRIVATE)
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)

        // Default to 30 days ago if this is the very first time syncing
        val lastSyncTime = prefs.getLong("last_sync_time", thirtyDaysAgo)
        var newestMessageTime = lastSyncTime

        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("address", "body", "date"),
            null,
            null,
            "date DESC" // Reads from newest texts to oldest
        )

        cursor?.use {
            val addressIndex = it.getColumnIndex("address")
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")

            while (it.moveToNext()) {
                val sender = it.getString(addressIndex) ?: ""
                val body = it.getString(bodyIndex) ?: ""
                val date = it.getLong(dateIndex)

                // 2. The cut-off switch: Stop reading if we hit messages we already synced!
                if (date <= lastSyncTime) break

                // Keep track of the timestamp of the newest message we process
                if (date > newestMessageTime) {
                    newestMessageTime = date
                }

                // 3. Hand the raw text to our smart parser
                val parsedTransaction = SmsParser.parseMessage(sender, body)

                if (parsedTransaction != null) {
                    // Keep the historical date instead of today's date
                    val historicalTx = parsedTransaction.copy(date = date)
                    db.transactionDao().insertTransaction(historicalTx)
                    transactionsFound++
                }
            }
        }

        // 4. Save the newest timestamp into the app's memory for next time
        prefs.edit().putLong("last_sync_time", newestMessageTime).apply()

        return@withContext transactionsFound
    }
}