package com.financeos.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.financeos.app.discovery.DiscoveryEngine
import com.financeos.app.screens.assets.AssetsScreen
import com.financeos.app.screens.expense.AddExpenseScreen
import com.financeos.app.state.PocketCFOState
import com.financeos.app.state.PocketCFOState.AppScreen

@Composable
fun PocketCFOApp() {

    val appState = remember {
        PocketCFOState()
    }

    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        when (appState.currentScreen) {

            AppScreen.DASHBOARD -> {

                DashboardScreen(

                    discoveryStatus = appState.discoveryStatus,

                    messagesRead = appState.messagesRead,

                    financialMessages = appState.financialMessagesFound,

                    banks = appState.banks,

                    creditCards = appState.creditCards,

                    transactions = appState.transactions,

                    onAssetsClick = {
                        appState.openAssets()
                    },

                    onDiscoveryClick = {

                        appState.startDiscovery()

                        val result = DiscoveryEngine()
                            .discoverFinancialMessages(context)

                        appState.discoveryCompleted(

                            totalMessages = result.messagesRead,

                            financialMessages = result.financialMessages,

                            banksFound = result.banks,

                            cardsFound = result.creditCards

                        )

                    },

                    onAddExpenseClick = {
                        appState.openAddExpense()
                    }

                )

            }

            AppScreen.ASSETS -> {

                AssetsScreen()

            }

            AppScreen.ADD_EXPENSE -> {

                AddExpenseScreen(

                    onSave = { amount, category, notes ->

                        appState.addExpense(
                            amount,
                            category,
                            notes
                        )

                        appState.openDashboard()

                    },

                    onCancel = {

                        appState.openDashboard()

                    }

                )

            }

        }

    }

}