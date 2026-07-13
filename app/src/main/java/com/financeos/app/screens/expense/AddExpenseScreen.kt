package com.financeos.app.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.financeos.app.data.FinanceDatabase
import com.financeos.app.data.Transaction
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State variables to hold the user's input
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Miscellaneous") }
    var paymentMethod by remember { mutableStateOf("Cash") }

    // Dropdown state for clean category selection
    val categories = listOf("Food & Dining", "Travel & Transport", "Bills & Utilities", "Shopping", "Entertainment", "Miscellaneous")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manual Entry") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (e.g., Office Lunch)") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- NATIVE COMPOSE DROPDOWN MENU ---
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true, // Prevents typing, forces dropdown selection
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                category = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = paymentMethod,
                onValueChange = { paymentMethod = it },
                label = { Text("Payment Method (e.g., Cash, Splitwise)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null && description.isNotBlank()) {
                        coroutineScope.launch {
                            val db = FinanceDatabase.getDatabase(context)
                            db.transactionDao().insertTransaction(
                                Transaction(
                                    amount = parsedAmount,
                                    type = "EXPENSE",
                                    category = category,
                                    date = System.currentTimeMillis(),
                                    paymentMethod = paymentMethod,
                                    description = description,
                                    isReconciled = true, // Flagged as manually audited!
                                    sender = "Manual Entry",
                                    rawMessage = "Manual Entry"
                                )
                            )
                            onBack() // Teleports you back to the Dashboard instantly
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                // The button stays greyed out until they enter a valid amount and description!
                enabled = amount.isNotBlank() && description.isNotBlank()
            ) {
                Text("Save Transaction")
            }
        }
    }
}