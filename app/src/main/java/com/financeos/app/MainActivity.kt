package com.financeos.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import com.financeos.app.ui.theme.FinanceOSTheme

class MainActivity : ComponentActivity() {

    private val requestSmsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {
            // SMS permission granted
        } else {
            // SMS permission denied
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            FinanceOSTheme {
                LaunchedEffect(Unit) {

                    requestSmsPermission.launch(
                        Manifest.permission.READ_SMS
                    )

                }
                PocketCFOApp()

            }

        }

    }

}