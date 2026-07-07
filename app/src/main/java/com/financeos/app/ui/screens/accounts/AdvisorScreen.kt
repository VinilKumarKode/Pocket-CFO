package com.financeos.app.ui.screens.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.financeos.app.YieldEngine
import com.financeos.app.CampaignScraper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvisorScreen() {
    val categories = listOf("Dining", "Fuel", "Travel", "Groceries", "Utilities", "General")
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    // These variables control the new Scraper button
    var isSyncing by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf("Ready to scan bank websites.") }
    val coroutineScope = rememberCoroutineScope() // Allows us to do internet tasks in the background

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consult CFO") },
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

            // --- NEW: The Web Scraper Command Center ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Market Intelligence", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(syncMessage, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            isSyncing = true
                            syncMessage = "Connecting to bank websites..."

                            // Send the CFO to hunt the internet in the background
                            coroutineScope.launch {
                                val foundOffers = CampaignScraper.huntForActiveCampaigns()
                                isSyncing = false
                                syncMessage = "Scan complete. Found ${foundOffers.size} active campaigns."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSyncing // Prevents you from spam-clicking the button
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hunting for deals...")
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = "Sync")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync Live Bank Offers")
                        }
                    }
                }
            }
            // -------------------------------------------

            Text("What are you about to purchase?", style = MaterialTheme.typography.titleMedium)

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("CFO Recommendation (Based on ₹1,000 spend):", style = MaterialTheme.typography.titleMedium)

            val recommendations = getRankedCardsForCategory(selectedCategory)

            // Ranked Cards
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(recommendations) { rank ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (rank.isBest) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(rank.cardName, style = MaterialTheme.typography.bodyLarge)
                            Text("Yield: ₹${rank.yieldValue}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

data class CardRank(val cardName: String, val yieldValue: Double, val isBest: Boolean)

fun getRankedCardsForCategory(category: String): List<CardRank> {
    val sampleSpend = 1000.0
    val myCards = listOf("Axis Bank", "SBI", "Jupiter", "bobcard", "Yes Bank")

    val calculatedList = myCards.map { card ->
        val yield = YieldEngine.calculateRewardValue(sampleSpend, category, card)
        Pair(card, yield)
    }

    val sortedList = calculatedList.sortedByDescending { it.second }

    return sortedList.mapIndexed { index, pair ->
        CardRank(
            cardName = pair.first,
            yieldValue = pair.second,
            isBest = index == 0 && pair.second > 0
        )
    }
}