package com.financeos.app.ui.screens.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.YieldEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvisorScreen() {
    // The list of categories you might spend money on
    val categories = listOf("Dining", "Fuel", "Travel", "Groceries", "Utilities", "General")

    // State to remember which category the user tapped
    var selectedCategory by remember { mutableStateOf(categories.first()) }

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
            Text("What are you about to purchase?", style = MaterialTheme.typography.titleMedium)

            // A horizontal scrollable row of category chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("CFO Recommendation (Based on ₹1,000 spend):", style = MaterialTheme.typography.titleMedium)

            // The CFO instantly calculates the best card for the selected category
            val recommendations = getRankedCardsForCategory(selectedCategory)

            // Display the ranked list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recommendations) { rank ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (rank.isBest) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
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

// A simple data class to hold the ranking results
data class CardRank(val cardName: String, val yieldValue: Double, val isBest: Boolean)

// The logic that asks the YieldEngine for the math
fun getRankedCardsForCategory(category: String): List<CardRank> {
    val sampleSpend = 1000.0
    val myCards = listOf("Axis Bank", "SBI", "Jupiter", "bobcard", "Yes Bank")

    // Calculate the yield for each card
    val calculatedList = myCards.map { card ->
        val yield = YieldEngine.calculateRewardValue(sampleSpend, category, card)
        Pair(card, yield)
    }

    // Sort them from highest yield to lowest
    val sortedList = calculatedList.sortedByDescending { it.second }

    // Convert to our CardRank objects, flagging the top one as "isBest"
    return sortedList.mapIndexed { index, pair ->
        CardRank(
            cardName = pair.first,
            yieldValue = pair.second,
            isBest = index == 0 && pair.second > 0 // Only highlight if it actually earns money
        )
    }
}