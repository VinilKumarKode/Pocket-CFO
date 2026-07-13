package com.financeos.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.data.FinanceDatabase
import com.financeos.app.data.UpcomingLiability
import com.financeos.app.utils.InboxScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var liabilityToEdit by remember { mutableStateOf<UpcomingLiability?>(null) }

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

    val hasNotificationPermission = NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    val allTransactions by viewModel.transactions.collectAsState(initial = emptyList())
    val pendingBills by viewModel.upcomingBills.collectAsState(initial = emptyList())
    val trashedBills by viewModel.trashedBills.collectAsState(initial = emptyList())
    val totalSpends = allTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

    var showTrashBin by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("CFO Command Center") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAddExpense() }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            if (!hasNotificationPermission) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unlock UPI Tracking", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Allow PocketCFO to read Google Pay and PhonePe notifications.", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Open Notification Settings") }
                    }
                }
            }

            Button(
                onClick = {
                    isSyncing = true
                    coroutineScope.launch {
                        val count = InboxScraper.syncHistoricalData(context)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Sync Complete! Found $count updates.", Toast.LENGTH_LONG).show()
                            isSyncing = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Sync")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isSyncing) "Scanning Inbox..." else "Force Manual SMS Sync")
            }

            if (pendingBills.isNotEmpty()) {
                Text("Upcoming Liabilities", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                    items(
                        items = pendingBills,
                        key = { it.id }
                    ) { bill ->

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    coroutineScope.launch {
                                        val db = FinanceDatabase.getDatabase(context)
                                        val trashedBill = bill.copy(
                                            isDeleted = true,
                                            deletedAt = System.currentTimeMillis()
                                        )
                                        db.upcomingLiabilityDao().updateLiability(trashedBill)
                                    }
                                    true
                                } else false
                            }
                        )

                        var isExpanded by remember { mutableStateOf(false) }

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                        MaterialTheme.colorScheme.error
                                    else
                                        Color.Transparent,
                                    label = "SwipeColor"
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 8.dp)
                                        .background(color, shape = CardDefaults.shape)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Move to Trash", tint = Color.White)
                                }
                            },
                            content = {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .clickable { isExpanded = !isExpanded },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(bill.title, fontWeight = FontWeight.Bold)
                                                Text("₹${"%.2f".format(bill.amountDue)}")
                                            }

                                            Row {
                                                IconButton(onClick = { liabilityToEdit = bill }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                                }
                                                IconButton(onClick = {
                                                    coroutineScope.launch {
                                                        val db = FinanceDatabase.getDatabase(context)
                                                        db.upcomingLiabilityDao().updateLiability(bill.copy(isPaid = true))
                                                    }
                                                }) { Icon(Icons.Default.Check, contentDescription = "Paid", tint = MaterialTheme.colorScheme.error) }
                                            }
                                        }

                                        AnimatedVisibility(visible = isExpanded) {
                                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.3f))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Source Audit:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                                Text(bill.rawMessage, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // --- THE COLLAPSIBLE RECYCLE BIN VIEW ---
            if (trashedBills.isNotEmpty()) {
                OutlinedButton(
                    onClick = { showTrashBin = !showTrashBin },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showTrashBin) "Hide Recycle Bin (${trashedBills.size})" else "View Recycle Bin (${trashedBills.size})")
                }

                AnimatedVisibility(visible = showTrashBin) {
                    LazyColumn(modifier = Modifier.heightIn(max = 180.dp)) {
                        items(trashedBills) { trashedBill ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(trashedBill.title, fontWeight = FontWeight.SemiBold)
                                        Text("₹${"%.2f".format(trashedBill.amountDue)}", style = MaterialTheme.typography.bodySmall)
                                    }

                                    // Restore Button (Flips flags back to active state)
                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            val db = FinanceDatabase.getDatabase(context)
                                            db.upcomingLiabilityDao().updateLiability(
                                                trashedBill.copy(isDeleted = false, deletedAt = null)
                                            )
                                        }
                                    }) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Restore Item", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
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

    if (liabilityToEdit != null) {
        var editTitle by remember { mutableStateOf(liabilityToEdit!!.title) }
        var editAmount by remember { mutableStateOf(liabilityToEdit!!.amountDue.toString()) }

        AlertDialog(
            onDismissRequest = { liabilityToEdit = null },
            title = { Text("Edit Upcoming Liability") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Bill Title & Date") }
                    )
                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = { editAmount = it },
                        label = { Text("Amount Due (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        val db = FinanceDatabase.getDatabase(context)
                        val updatedLiability = liabilityToEdit!!.copy(
                            title = editTitle,
                            amountDue = editAmount.toDoubleOrNull() ?: liabilityToEdit!!.amountDue
                        )
                        db.upcomingLiabilityDao().updateLiability(updatedLiability)
                        liabilityToEdit = null
                    }
                }) { Text("Save Changes") }
            },
            dismissButton = {
                TextButton(onClick = { liabilityToEdit = null }) { Text("Cancel") }
            }
        )
    }
}