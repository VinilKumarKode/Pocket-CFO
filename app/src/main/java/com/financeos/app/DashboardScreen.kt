package com.financeos.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.data.Transaction
import com.financeos.app.data.FinanceDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- NEW PERMISSION LOGIC ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted! The app can now listen to SMS.
        }
    }

    LaunchedEffect(Unit) {
        // When the screen loads, check if we already have permission. If not, ask for it!
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
        }
    }
    // ----------------------------

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
                    coroutineScope.launch {
                        val db = FinanceDatabase.getDatabase(context)
                        val simulatedTransaction = Transaction(
                            amount = 1500.00,
                            type = "EXPENSE",
                            category = com.financeos.app.utils.CategoryEngine.getCategoryForMerchant("Zomato Online"), // Testing the engine!
                            date = System.currentTimeMillis(),
                            paymentMethod = "Card *1234",
                            rewardPointsEarned = 15.0,
                            description = "Zomato Online",
                            isReconciled = false,
                            sender = "AD-HDFCBK",
                            rawMessage = "Rs. 1500.00 debited from a/c *1234 at Zomato Online on 07-Jul-26. Ref: 89347593. Not you? Call 1800-456-7890."
                        )
                        db.transactionDao().insertTransaction(simulatedTransaction)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simulate Received Bank SMS")
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Spends This Month", style = MaterialTheme.typography.titleMedium)
                    Text("₹ ${"%.2f".format(totalSpends)}", style = MaterialTheme.typography.headlineLarge)
                }
            }

            if (unreconciledCount > 0) {
                Text("Pending Verifications:", style = MaterialTheme.typography.titleSmall)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(pendingReceipts) { receipt ->
                        var isExpanded by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${receipt.description} - ₹${receipt.amount}\n(${receipt.paymentMethod})", modifier = Modifier.weight(1f))

                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            val db = FinanceDatabase.getDatabase(context)
                                            val verifiedReceipt = receipt.copy(isReconciled = true)
                                            db.transactionDao().updateTransaction(verifiedReceipt)
                                        }
                                    }) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Mark as Reconciled",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 12.dp)) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        val dateString = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(receipt.date))

                                        Text("Sender: ${receipt.sender}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                        Text("Date: $dateString", style = MaterialTheme.typography.labelSmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Raw SMS:", style = MaterialTheme.typography.labelSmall)
                                        Text(receipt.rawMessage, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
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