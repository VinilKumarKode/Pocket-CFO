package com.financeos.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeos.app.state.PocketCFOState
import com.financeos.app.ui.components.TransactionCard

@Composable
fun DashboardScreen(
    state: PocketCFOState,
    onNavigateToExpense: () -> Unit,
    onNavigateToIncome: () -> Unit,
    onNavigateToAssets: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Financial Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Net Worth", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = "₹${"%.2f".format(state.netWorth)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Income", style = MaterialTheme.typography.labelMedium)
                        Text("₹${"%.2f".format(state.totalIncome)}", color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Expense", style = MaterialTheme.typography.labelMedium)
                        Text("₹${"%.2f".format(state.totalExpense)}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onNavigateToExpense, modifier = Modifier.weight(1f)) {
                Text("+ Expense")
            }
            Button(onClick = onNavigateToIncome, modifier = Modifier.weight(1f)) {
                Text("+ Income")
            }
            OutlinedButton(onClick = onNavigateToAssets, modifier = Modifier.weight(1f)) {
                Text("Assets")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Professional Transaction Ledger
        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (state.transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No transactions yet.", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // This line is now fixed
                items(state.transactions.sortedByDescending { it.timestamp }) { transaction ->
                    TransactionCard(transaction = transaction)
                }
            }
        }
    }
}