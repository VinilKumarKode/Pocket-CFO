package com.financeos.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.state.PocketCFOState
import com.financeos.app.ui.components.TransactionCard

@Composable
fun DashboardScreen(
    state: PocketCFOState,
    onNavigateToExpense: () -> Unit,
    onNavigateToIncome: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Transactions") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        val filteredTransactions = state.transactions.filter {
            it.category.contains(searchQuery, ignoreCase = true)
        }.sortedByDescending { it.timestamp }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredTransactions) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onClick = { state.deleteTransaction(transaction) } // Delete logic
                )
            }
        }
    }
}