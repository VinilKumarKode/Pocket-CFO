package com.financeos.app

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.data.Transaction
import com.financeos.app.data.FinanceDatabase
import com.financeos.app.utils.CategoryEngine
import com.financeos.app.utils.InboxScraper
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // State to track which transaction we are editing
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions -> }

    LaunchedEffect(Unit) {
        val readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
        val receivePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)
        if (readPermission != PackageManager.PERMISSION_GRANTED || receivePermission != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS))
        }
    }

    val allTransactions by viewModel.transactions.collectAsState(initial = emptyList())
    val totalSpends = allTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val pendingReceipts = allTransactions.filter { !it.isReconciled }
    val unreconciledCount = pendingReceipts.size
    val totalRewards = allTransactions.sumOf { it.rewardPointsEarned ?: 0.0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CFO Command Center") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddExpense() },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, contentDescription = "Add Manual Expense") }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    isSyncing = true
                    coroutineScope.launch {
                        val foundCount = InboxScraper.syncHistoricalData(context)
                        Toast.makeText(context, "Scraped $foundCount new transactions!", Toast.LENGTH_LONG).show()
                        isSyncing = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSyncing,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) { Text(if (isSyncing) "Scanning Inbox..." else "Sync Last 30 Days of SMS") }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Spends This Month", style = MaterialTheme.typography.titleMedium)
                    Text("₹ ${"%.2f".format(totalSpends)}", style = MaterialTheme.typography.headlineLarge)
                }
            }

            if (unreconciledCount > 0) {
                Text("Pending Verifications:", style = MaterialTheme.typography.titleSmall)

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    items(pendingReceipts) { receipt ->
                        var isExpanded by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${receipt.description}\n₹${receipt.amount} (${receipt.paymentMethod})", modifier = Modifier.weight(1f))

                                    // --- THE NEW EDIT BUTTON ---
                                    IconButton(onClick = { transactionToEdit = receipt }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.secondary)
                                    }

                                    // THE APPROVE BUTTON
                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            val db = FinanceDatabase.getDatabase(context)
                                            db.transactionDao().updateTransaction(receipt.copy(isReconciled = true))
                                        }
                                    }) { Icon(Icons.Default.Check, contentDescription = "Approve", tint = MaterialTheme.colorScheme.primary) }
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 12.dp)) {
                                        HorizontalDivider()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Category: ${receipt.category}", style = MaterialTheme.typography.labelMedium)
                                        Text("Raw SMS: ${receipt.rawMessage}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            Button(onClick = { onNavigateToAnalytics() }, modifier = Modifier.fillMaxWidth()) { Text("View Detailed Analytics") }
        }
    }

    // --- THE EDIT POPUP DIALOG ---
    if (transactionToEdit != null) {
        var editAmount by remember { mutableStateOf(transactionToEdit!!.amount.toString()) }
        var editCategory by remember { mutableStateOf(transactionToEdit!!.category) }
        var editMerchant by remember { mutableStateOf(transactionToEdit!!.description) }

        AlertDialog(
            onDismissRequest = { transactionToEdit = null },
            title = { Text("Edit Transaction") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editAmount, onValueChange = { editAmount = it },
                        label = { Text("Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(value = editMerchant, onValueChange = { editMerchant = it }, label = { Text("Merchant") })
                    OutlinedTextField(value = editCategory, onValueChange = { editCategory = it }, label = { Text("Category") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        val db = FinanceDatabase.getDatabase(context)
                        val updatedTx = transactionToEdit!!.copy(
                            amount = editAmount.toDoubleOrNull() ?: transactionToEdit!!.amount,
                            category = editCategory,
                            description = editMerchant,
                            isReconciled = true // Automatically approve it since you just verified it!
                        )
                        db.transactionDao().updateTransaction(updatedTx)
                        transactionToEdit = null
                    }
                }) { Text("Save & Approve") }
            },
            dismissButton = {
                TextButton(onClick = { transactionToEdit = null }) { Text("Cancel") }
            }
        )
    }
}