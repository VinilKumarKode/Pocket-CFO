package com.financeos.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.financeos.app.data.FinanceDatabase
import com.financeos.app.utils.InboxScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val db = FinanceDatabase.getDatabase(applicationContext)

            // 1. Run the core inbound scraping routines for SMS and entities
            InboxScraper.syncHistoricalData(applicationContext)

            // 2. RUN THE RETENTION ENGINE
            // Define retention period: 30 days in milliseconds
            val retentionPeriodMs = 30L * 24 * 60 * 60 * 1000
            val purgeThreshold = System.currentTimeMillis() - retentionPeriodMs

            // Execute the bulk database sweep
            db.upcomingLiabilityDao().purgeOldTrashedLiabilities(purgeThreshold)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}