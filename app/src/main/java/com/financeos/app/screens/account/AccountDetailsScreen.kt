package com.financeos.app.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeos.app.state.PocketCFOState
import com.financeos.app.ui.components.TransactionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(state: PocketCFOState, onBack: () -> Unit) {
    val accountId = state.selectedAccountId
    val account = state.accounts.find { it.id.toString() == accountId }

    // Explicitly casting the filter to a List of Transactions to stop the "inference" error
    val accountTransactions: List<com.financeos.app.data.Transaction> = state.transactions
        .filter { it.accountId == accountId }
        .sortedByDescending { it.timestamp }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(account?.name ?: "Account Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            if (account != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Current Balance", style = MaterialTheme.typography.labelMedium)
                        Text("₹${"%.2f".format(account.balance)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Account Ledger", style = MaterialTheme.typography.titleLarge)

            if (accountTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No transactions found.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(accountTransactions) { transaction ->
                        TransactionCard(transaction = transaction, onClick = { state.deleteTransaction(transaction) })
                    }
                }
            }
        }
    }
}