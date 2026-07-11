package com.financeos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.workers.SyncWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- THE SHADOW SYNC SCHEDULER ---
        // This tells Android to run your scraper in the background every 6 hours
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BackgroundInboxSync",
            ExistingPeriodicWorkPolicy.KEEP, // Ensures it doesn't create duplicate schedules
            syncRequest
        )

        setContent {
            val viewModel: FinanceViewModel = viewModel()
            PocketCFOApp(viewModel = viewModel)
        }
    }
}