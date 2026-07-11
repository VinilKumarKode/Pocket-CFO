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

        val prefs = context.getSharedPreferences("PocketCFO_Prefs", Context.MODE_PRIVATE)
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)

        val lastSyncTime = prefs.getLong("last_sync_time", thirtyDaysAgo)
        var newestMessageTime = lastSyncTime

        val pastHistory = db.transactionDao().getAllTransactionsSync()

        // Notice we changed "DESC" to "ASC".
        // Reading oldest to newest ensures your live balance always reflects the MOST RECENT text!
        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("address", "body", "date"),
            null,
            null,
            "date ASC"
        )

        cursor?.use {
            val addressIndex = it.getColumnIndex("address")
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")

            while (it.moveToNext()) {
                val sender = it.getString(addressIndex) ?: ""
                val body = it.getString(bodyIndex) ?: ""
                val date = it.getLong(dateIndex)

                if (date <= lastSyncTime) continue

                if (date > newestMessageTime) {
                    newestMessageTime = date
                }

                // --- NEW: Run Brain 2 (Asset Discovery) ---
                val parsedEntity = SmsParser.parseFinancialEntity(sender, body)
                if (parsedEntity != null) {
                    val existingEntity = db.financialEntityDao().findEntity(parsedEntity.name, parsedEntity.lastFourDigits)

                    if (existingEntity == null) {
                        // We discovered a completely new Bank/Card! Save it.
                        db.financialEntityDao().insertEntity(parsedEntity)
                    } else {
                        // We already know about this account, just update the live balance!
                        val updatedEntity = existingEntity.copy(
                            balance = parsedEntity.balance,
                            creditLimit = parsedEntity.creditLimit ?: existingEntity.creditLimit
                        )
                        db.financialEntityDao().insertEntity(updatedEntity)
                    }
                }

                // --- ORIGINAL: Run Brain 1 (Transaction Engine) ---
                val parsedTransaction = SmsParser.parseMessage(sender, body)
                if (parsedTransaction != null) {
                    val historicalTx = parsedTransaction.copy(date = date)
                    val smartTx = LearningEngine.applyUserMemory(historicalTx, pastHistory)
                    db.transactionDao().insertTransaction(smartTx)
                    transactionsFound++
                }
            }
        }

        prefs.edit().putLong("last_sync_time", newestMessageTime).apply()

        return@withContext transactionsFound
    }
}