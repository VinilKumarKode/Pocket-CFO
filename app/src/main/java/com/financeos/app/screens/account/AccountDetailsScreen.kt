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
fun AccountDetailsScreen(
    state: PocketCFOState,
    onBack: () -> Unit
) {
    // 1. Identify which account the user clicked
    val accountId = state.selectedAccountId
    val account = state.accounts.find { it.id.toString() == accountId }

    // 2. Filter the ledger to ONLY show transactions for this account
    val accountTransactions = state.transactions
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (account != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Current Balance", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "₹${"%.2f".format(account.balance)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Institution: ${account.institution}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Type: ${account.type.name}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Account Ledger",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (accountTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No transactions found for this account.", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(accountTransactions) { transaction ->
                        TransactionCard(transaction = transaction)
                    }
                }
            }
        }
    }
}