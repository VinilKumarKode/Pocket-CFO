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
    // This securely observes your local Room database in real-time
    val transactions by viewModel.transactions.collectAsState()

    // CFO Calculations: The app does the math instantly as data arrives
    val totalSpends = transactions.filter { it.type == "Debit" }.sumOf { it.amount }
    val unreconciledCount = transactions.count { !it.isReconciled }
    val totalRewards = transactions.sumOf { it.rewardPointsEarned ?: 0.0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CFO Command Center") },
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

            // 1. Cash Flow Overview
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Spends", style = MaterialTheme.typography.titleMedium)
                    Text("₹ ${"%.2f".format(totalSpends)}", style = MaterialTheme.typography.headlineLarge)
                }
            }

            // 2. Audit & Reconciliation Warning
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    // Dynamically turns red if you have missing receipts!
                    containerColor = if (unreconciledCount > 0) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Audit Status", style = MaterialTheme.typography.titleMedium)
                    Text("$unreconciledCount Unreconciled Entries", style = MaterialTheme.typography.bodyLarge)
                    if (unreconciledCount > 0) {
                        Text("Awaiting statement verification.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("All books balanced.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 3. Reward Yield Optimizer
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Rewards Generated", style = MaterialTheme.typography.titleMedium)
                    Text("₹ ${"%.2f".format(totalRewards)}", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}