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

                // BRAIN 2: Asset Discovery
                val parsedEntity = SmsParser.parseFinancialEntity(sender, body)
                if (parsedEntity != null) {
                    val existingEntity = db.financialEntityDao().findEntity(parsedEntity.name, parsedEntity.lastFourDigits)
                    if (existingEntity == null) {
                        db.financialEntityDao().insertEntity(parsedEntity)
                    } else {
                        val updatedEntity = existingEntity.copy(
                            balance = parsedEntity.balance,
                            creditLimit = parsedEntity.creditLimit ?: existingEntity.creditLimit
                        )
                        db.financialEntityDao().insertEntity(updatedEntity)
                    }
                }

                // BRAIN 3: Timeline Engine (Upcoming Bills)
                val parsedLiability = SmsParser.parseUpcomingLiability(sender, body)
                if (parsedLiability != null) {
                    val existingBill = db.upcomingLiabilityDao().findPendingLiability(parsedLiability.title, parsedLiability.amountDue)
                    if (existingBill == null) {
                        db.upcomingLiabilityDao().insertLiability(parsedLiability)
                    }
                }

                // BRAIN 1: Transaction Engine
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