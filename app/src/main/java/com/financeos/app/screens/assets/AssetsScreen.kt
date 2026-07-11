package com.financeos.app.screens.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.data.FinancialEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    // 1. Listen to the database for discovered accounts
    val entities by viewModel.financialEntities.collectAsState(initial = emptyList())

    // 2. Separate them into Assets and Liabilities automatically
    val bankAccounts = entities.filter { it.type == "BANK_ACCOUNT" }
    val creditCards = entities.filter { it.type == "CREDIT_CARD" }

    // 3. Calculate Live Net Worth!
    val totalAssets = bankAccounts.sumOf { it.balance }
    val totalLiabilities = creditCards.sumOf { it.balance }
    val netWorth = totalAssets - totalLiabilities

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Net Worth & Assets") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- THE LIVE NET WORTH CARD ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Live Net Worth", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "₹ ${"%.2f".format(netWorth)}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Assets: ₹${"%.2f".format(totalAssets)}", color = MaterialTheme.colorScheme.primary)
                        Text("Liabilities: ₹${"%.2f".format(totalLiabilities)}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // --- DISCOVERED BANK ACCOUNTS ---
            if (bankAccounts.isNotEmpty()) {
                Text("Discovered Bank Accounts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(bankAccounts) { account ->
                        AccountCard(account)
                    }
                }
            }

            // --- DISCOVERED CREDIT CARDS ---
            if (creditCards.isNotEmpty()) {
                Text("Discovered Credit Cards", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(creditCards) { card ->
                        AccountCard(card)
                    }
                }
            }

            if (bankAccounts.isEmpty() && creditCards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Scanning inbox for financial accounts...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// A reusable UI component for drawing each account beautifully
@Composable
fun AccountCard(entity: FinancialEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(entity.name, style = MaterialTheme.typography.titleMedium)
                Text("Ending in *${entity.lastFourDigits}", style = MaterialTheme.typography.bodyMedium)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text("₹ ${"%.2f".format(entity.balance)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (entity.creditLimit != null) {
                    Text("Limit: ₹${"%.2f".format(entity.creditLimit)}", style = MaterialTheme.typography.labelSmall)
                } else {
                    Text("Available", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}