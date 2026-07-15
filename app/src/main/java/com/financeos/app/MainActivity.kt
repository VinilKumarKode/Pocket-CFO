package com.financeos.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        // --- NATIVE LIFE-CYCLE PERMISSION GATE ---
        checkAndRequestSmsPermissions()

        // --- THE SHADOW SYNC SCHEDULER ---
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()

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

    private fun checkAndRequestSmsPermissions() {
        val readSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        val receiveSms = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)

        val permissionsToRequest = mutableListOf<String>()
        if (readSms != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_SMS)
        }
        if (receiveSms != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECEIVE_SMS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Native activity permissions invocation avoids the Compose 16-bit bug
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 101)
        }
    }
}