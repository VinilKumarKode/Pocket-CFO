package com.financeos.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.components.DashboardCard

@Composable
fun PocketCFOApp() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Pocket CFO",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Know. Plan. Grow.",
                style = MaterialTheme.typography.bodyLarge
            )

            DashboardCard(
                title = "Bills Due Today",
                value = "₹12,500",
                subtitle = "2 Bills Pending"
            )

            DashboardCard(
                title = "Credit Card Due",
                value = "₹18,400",
                subtitle = "Tomorrow"
            )

            DashboardCard(
                title = "Cash Available",
                value = "₹3,42,500",
                subtitle = "Across 4 Bank Accounts"
            )

            DashboardCard(
                title = "Today's Smart Advice",
                value = "Use SBI BPCL Octane",
                subtitle = "Fuel Purchase"
            )

        }

    }

}