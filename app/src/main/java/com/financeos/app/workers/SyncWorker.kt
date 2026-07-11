package com.financeos.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.financeos.app.utils.InboxScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1. Fire up the exact same scraper we built earlier
            val newTransactions = InboxScraper.syncHistoricalData(applicationContext)

            // 2. Tell Android the background job was a success
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // If something goes wrong (e.g., phone is updating), try again later
            Result.retry()
        }
    }
}