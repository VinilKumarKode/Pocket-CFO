package com.financeos.app.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    val context = LocalContext.current
    var authError by remember { mutableStateOf<String?>(null) }

    fun authenticate() {
        val activity = context as? FragmentActivity
        if (activity == null) {
            authError = "Security Context Error."
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // If they cancel or fail too many times, we just show an error text
                    authError = "Authentication failed: $errString"
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // THE VAULT OPENS!
                    onAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    authError = "Biometric not recognized. Try again."
                }
            })

        // This configuration allows Fingerprint/Face OR the 6-digit Device PIN!
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("PocketCFO Security")
            .setSubtitle("Unlock your financial ledger")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // Automatically trigger the prompt when the screen loads
    LaunchedEffect(Unit) {
        authenticate()
    }

    // The UI behind the prompt
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Lock,
            contentDescription = "Vault Locked",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("PocketCFO is Locked", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        if (authError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(authError!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { authenticate() }) {
                Text("Tap to Unlock")
            }
        }
    }
}