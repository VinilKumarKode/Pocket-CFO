package com.financeos.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.workers.SyncWorker
import java.util.concurrent.TimeUnit

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- THE SHADOW SYNC SCHEDULER ---
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()

        // FIX: Removed named arguments since WorkManager is a Java class under the hood
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BackgroundInboxSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        setContent {
            val viewModel: FinanceViewModel = viewModel()
            PocketCFOApp(viewModel = viewModel)
        }
    }
}