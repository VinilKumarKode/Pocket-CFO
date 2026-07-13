package com.financeos.app.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.app.Notification
import com.financeos.app.data.FinanceDatabase
import com.financeos.app.utils.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationInterceptorService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    // A list of the package names for popular Indian UPI apps
    private val targetApps = listOf(
        "com.google.android.apps.nbu.paisa.user", // Google Pay
        "com.phonepe.app",                        // PhonePe
        "net.one97.paytm",                        // Paytm
        "com.dreamplug.androidapp"                // Cred
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName

        // Ignore notifications unless they are from our target finance apps
        if (targetApps.any { packageName.contains(it) }) {
            val extras = sbn.notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
            val text = extras.getString(Notification.EXTRA_TEXT) ?: ""

            // Map the ugly package name to a clean display name
            val appName = when {
                packageName.contains("paisa") -> "Google Pay"
                packageName.contains("phonepe") -> "PhonePe"
                packageName.contains("paytm") -> "Paytm"
                packageName.contains("dreamplug") -> "Cred"
                else -> "UPI App"
            }

            // Hand the notification to Brain 4!
            val transaction = SmsParser.parseUpiNotification(appName, title, text)

            if (transaction != null) {
                serviceScope.launch {
                    val db = FinanceDatabase.getDatabase(applicationContext)
                    db.transactionDao().insertTransaction(transaction)
                }
            }
        }
    }
}