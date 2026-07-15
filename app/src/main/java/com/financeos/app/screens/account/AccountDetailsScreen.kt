package com.financeos.app.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeos.app.state.PocketCFOState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    state: PocketCFOState,
    onBack: () -> Unit
) {
    val accountId = state.selectedAccountId
    val account = state.accounts.find { it.id.toString() == accountId }

    // Safe filter: Matches transactions using the parsed paymentMethod string and sorts by date
    val accountTransactions = state.transactions
        .filter { account != null && it.paymentMethod.contains(account.name, ignoreCase = true) }
        .sortedByDescending { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(account?.name ?: "Account Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (account != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Current Balance", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "₹ ${"%.2f".format(account.balance)}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Account Ending in: *${account.lastFourDigits}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text("Activity History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (accountTransactions.isEmpty()) {
                Text("No recent transactions found for this account.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accountTransactions) { transaction ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(transaction.description, fontWeight = FontWeight.SemiBold)
                                    Text(transaction.category, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    text = "${if (transaction.type == "EXPENSE") "-" else "+"} ₹${"%.2f".format(transaction.amount)}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (transaction.type == "EXPENSE") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}