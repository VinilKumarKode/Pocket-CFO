package com.financeos.app.screens.income

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.state.PocketCFOState
import com.financeos.app.utils.DataExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyDashboardScreen(
    viewModel: FinanceViewModel,
    state: PocketCFOState
) {
    val context = LocalContext.current
    val allTransactions by viewModel.transactions.collectAsState(initial = emptyList())
    val approvedExpenses = allTransactions.filter { it.type == "EXPENSE" && it.isReconciled }
    val totalApprovedSpend = approvedExpenses.sumOf { it.amount }

    val categoryTotals = approvedExpenses
        .groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics & Insights") },
                navigationIcon = {
                    IconButton(onClick = { state.openDashboard() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard")
                    }
                },
                // --- THE NEW EXPORT BUTTON IN THE TOP RIGHT ---
                actions = {
                    IconButton(onClick = {
                        DataExporter.exportDatabaseToCSV(context, allTransactions)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export to CSV")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Approved Spends", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "₹ ${"%.2f".format(totalApprovedSpend)}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text("Spending by Category", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

            if (categoryTotals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No approved expenses yet. Reconcile some receipts on your dashboard!", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categoryTotals) { (category, amount) ->
                        val percentage = if (totalApprovedSpend > 0) (amount / totalApprovedSpend).toFloat() else 0f

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(category, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("₹${"%.2f".format(amount)}", style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { percentage },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}