package com.financeos.app.utils

import android.content.Context
import android.net.Uri
import com.financeos.app.data.FinanceDatabase
import java.util.Calendar

object InboxScraper {

    suspend fun syncHistoricalData(context: Context): Map<String, Int> {
        val db = FinanceDatabase.getDatabase(context)
        var smsProcessed = 0
        var transactionsFound = 0
        var entitiesFound = 0

        // 1. LIMIT HISTORICAL ARCHIVE TO EXACTLY 2 YEARS BACKDATE
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -2)
        val twoYearsAgoMs = cal.timeInMillis

        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("address", "body", "date"),
            "date >= ?",
            arrayOf(twoYearsAgoMs.toString()),
            "date DESC" // Process the newest statements first
        )

        cursor?.use {
            val addressIndex = it.getColumnIndex("address")
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")

            while (it.moveToNext()) {
                smsProcessed++
                val sender = it.getString(addressIndex) ?: ""
                val body = it.getString(bodyIndex) ?: ""
                val msgDate = it.getLong(dateIndex)

                val transaction = SmsParser.parseMessage(sender, body)
                if (transaction != null) {
                    db.transactionDao().insertTransaction(transaction.copy(date = msgDate))
                    transactionsFound++
                }

                // 2. INTELLIGENT CARD CLUBBING ENGINE
                val parsedEntity = SmsParser.parseFinancialEntity(sender, body)
                if (parsedEntity != null && parsedEntity.lastFourDigits.isNotBlank()) {
                    val existingEntity = db.financialEntityDao().getEntityByDetails(
                        bankName = parsedEntity.name,
                        lastFour = parsedEntity.lastFourDigits
                    )

                    if (existingEntity == null) {
                        // New card/account discovered
                        db.financialEntityDao().insertEntity(parsedEntity.copy(lastUpdated = msgDate))
                        entitiesFound++
                    } else {
                        // Existing account discovered. Overwrite ONLY if this text is newer
                        if (msgDate > existingEntity.lastUpdated) {
                            val updatedEntity = existingEntity.copy(
                                balance = parsedEntity.balance,
                                creditLimit = parsedEntity.creditLimit ?: existingEntity.creditLimit,
                                lastUpdated = msgDate
                            )
                            db.financialEntityDao().updateEntity(updatedEntity)
                        }
                    }
                }
            }
        }

        return mapOf(
            "sms" to smsProcessed,
            "transactions" to transactionsFound,
            "entities" to entitiesFound
        )
    }
}