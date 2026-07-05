package com.financeos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    // This securely observes your Room database in real-time
    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Command Center") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Placeholder: Cash Flow Overview Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Net Cash Flow", style = MaterialTheme.typography.titleMedium)
                    Text("₹ 0.00", style = MaterialTheme.typography.headlineLarge)
                }
            }

            // Placeholder: Audit & Reconciliation Warning
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pending Verification", style = MaterialTheme.typography.titleMedium)
                    Text("0 Unreconciled Entries", style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Placeholder: Reward Yield Optimizer
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Credit Card Rewards Yield", style = MaterialTheme.typography.titleMedium)
                    Text("Tracking active...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}