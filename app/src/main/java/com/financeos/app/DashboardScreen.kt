package com.financeos.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.data.Transaction
import com.financeos.app.data.FinanceDatabase
import com.financeos.app.utils.InboxScraper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToAddExpense: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSyncing by remember { mutableStateOf(false) }

    val allTransactions by viewModel.transactions.collectAsState(initial = emptyList())
    val pendingBills by viewModel.upcomingBills.collectAsState(initial = emptyList()) // Reading from DB

    val totalSpends = allTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val pendingReceipts = allTransactions.filter { !it.isReconciled }

    Scaffold(
        topBar = { TopAppBar(title = { Text("CFO Command Center") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAddExpense() }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Button(onClick = {
                isSyncing = true
                coroutineScope.launch {
                    InboxScraper.syncHistoricalData(context)
                    isSyncing = false
                }
            }, modifier = Modifier.fillMaxWidth()) { Text(if (isSyncing) "Syncing..." else "Sync Data") }

            // --- TIMELINE UI ---
            if (pendingBills.isNotEmpty()) {
                Text("Upcoming Liabilities", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(pendingBills) { bill ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bill.title, fontWeight = FontWeight.Bold)
                                    Text("₹${"%.2f".format(bill.amountDue)}")
                                }
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        val db = FinanceDatabase.getDatabase(context)
                                        db.upcomingLiabilityDao().updateLiability(bill.copy(isPaid = true))
                                    }
                                }) { Icon(Icons.Default.Check, contentDescription = "Paid") }
                            }
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Spends", style = MaterialTheme.typography.titleMedium)
                    Text("₹ ${"%.2f".format(totalSpends)}", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Button(onClick = { onNavigateToAnalytics() }, modifier = Modifier.fillMaxWidth()) { Text("View Detailed Analytics") }
        }
    }
}