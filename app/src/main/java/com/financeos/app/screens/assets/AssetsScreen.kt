package com.financeos.app.screens.assets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.components.DashboardCard

@Composable
fun AssetsScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Assets",
            style = MaterialTheme.typography.headlineMedium
        )

        DashboardCard(
            title = "🏦 Bank Accounts",
            value = "4 Accounts",
            subtitle = "Tap to Manage"
        )

        DashboardCard(
            title = "💳 Credit Cards",
            value = "7 Cards",
            subtitle = "Tap to Manage"
        )

        DashboardCard(
            title = "🏠 Loans",
            value = "3 Loans",
            subtitle = "Tap to Manage"
        )

        DashboardCard(
            title = "🛡 Insurance",
            value = "2 Policies",
            subtitle = "Tap to Manage"
        )
    }
}

