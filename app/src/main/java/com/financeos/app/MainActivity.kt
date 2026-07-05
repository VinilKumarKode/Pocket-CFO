package com.financeos.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeos.app.data.FinanceDatabase
import com.financeos.app.data.TransactionRepository
import com.financeos.app.ui.theme.FinanceOSTheme
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {

    private val requestSmsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // SMS permission granted - We will trigger the background worker here later
        } else {
            // SMS permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // 1. Initialize the local database and repository
        val database = FinanceDatabase.getDatabase(this)
        val repository = TransactionRepository(database.transactionDao())
        val factory = FinanceViewModelFactory(repository)

        setContent {
            FinanceOSTheme {

                // 2. Request SMS permission when the app starts
                LaunchedEffect(Unit) {
                    requestSmsPermission.launch(Manifest.permission.READ_SMS)
                }

                // 3. Instantiate the ViewModel using our factory
                val viewModel: FinanceViewModel = viewModel(factory = factory)

                // 4. Launch your main app container, passing the ViewModel down
                PocketCFOApp(viewModel = viewModel)
            }
        }
    }
}