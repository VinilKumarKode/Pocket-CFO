package com.financeos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    // We are using temporary test data so you can see exactly how the Audit works on your phone
    var pendingReceipts by remember {
        mutableStateOf(
            listOf(
                "Zomato - ₹450.00 (Axis Bank)",
                "HPCL Petrol - ₹1200.00 (SBI Card)",
                "Amazon - ₹899.00 (Jupiter)"
            )
        )
    }

    val totalSpends = 2549.00 // Temporary fake total
    val unreconciledCount = pendingReceipts.size
    val totalRewards = 65.50 // Temporary fake rewards

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
                    Text("Total Spends This Month", style = MaterialTheme.typography.titleMedium)
                    Text("₹ ${"%.2f".format(totalSpends)}", style = MaterialTheme.typography.headlineLarge)
                }
            }

            // 2. Audit & Reconciliation Warning
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (unreconciledCount > 0) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Shadow Statement Audit", style = MaterialTheme.typography.titleMedium)
                    Text("$unreconciledCount Unreconciled Entries", style = MaterialTheme.typography.bodyLarge)

                    if (unreconciledCount > 0) {
                        Text("Please cross-check these with your bank statement.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("All books balanced. Your CFO is happy.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 3. THE NEW FEATURE: The Audit List
            // This list only shows up if you have missing receipts!
            if (unreconciledCount > 0) {
                Text("Pending Verifications:", style = MaterialTheme.typography.titleSmall)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f) // Takes up remaining space on screen
                ) {
                    items(pendingReceipts) { receipt ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(receipt, modifier = Modifier.weight(1f))

                                // The Reconcile Button
                                IconButton(
                                    onClick = {
                                        // This simulates checking it against the bank and marking it safe
                                        pendingReceipts = pendingReceipts.filter { it != receipt }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Mark as Reconciled",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 4. Reward Yield Optimizer (This shows up nicely once you finish the audit!)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Rewards Generated", style = MaterialTheme.typography.titleMedium)
                        Text("₹ ${"%.2f".format(totalRewards)}", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}