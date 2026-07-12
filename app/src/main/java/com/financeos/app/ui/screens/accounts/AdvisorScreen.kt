package com.financeos.app.ui.screens.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.utils.YieldEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvisorScreen(viewModel: FinanceViewModel) {
    // 1. Pull the user's discovered cards from the database
    val entities by viewModel.financialEntities.collectAsState(initial = emptyList())
    val creditCards = entities.filter { it.type == "CREDIT_CARD" }

    // 2. The core categories we want to optimize for the user
    val categoriesToOptimize = listOf("Food & Dining", "Bills & Utilities", "Shopping", "Travel & Transport")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CFO Intelligence") },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Advisor", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yield Optimization Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Here is exactly which card from your wallet you should use to maximize cashback for every purchase.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text("Your Action Plan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            if (creditCards.isEmpty()) {
                Text("No credit cards discovered yet. Sync your inbox to unlock optimization strategies!", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categoriesToOptimize) { category ->

                        // Pass the specific category and the user's real cards to the brain!
                        val recommendation = YieldEngine.getBestCardForCategory(category, creditCards)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(recommendation, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}