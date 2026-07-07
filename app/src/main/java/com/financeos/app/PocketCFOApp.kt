package com.financeos.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import com.financeos.app.ui.screens.DashboardScreen
import com.financeos.app.screens.MonthlyDashboardScreen
import com.financeos.app.screens.account.AccountDetailsScreen
import com.financeos.app.screens.assets.AssetsScreen
import com.financeos.app.state.PocketCFOState
import com.financeos.app.state.PocketCFOState.AppScreen
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.ui.screens.accounts.AdvisorScreen

@Composable
fun PocketCFOApp(viewModel: FinanceViewModel) {

    val appState = remember { PocketCFOState() }
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (appState.currentScreen) {
            AppScreen.DASHBOARD -> {
                // Local state to track which bottom tab is active
                var currentBottomTab by remember { mutableStateOf("Dashboard") }

                // The new Bottom Navigation Bar, safely nested inside your main view
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                                label = { Text("Dashboard") },
                                selected = currentBottomTab == "Dashboard",
                                onClick = { currentBottomTab = "Dashboard" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Advisor") },
                                label = { Text("Advisor") },
                                selected = currentBottomTab == "Advisor",
                                onClick = { currentBottomTab = "Advisor" }
                            )
                        }
                    }
                ) { innerPadding ->
                    // This switches the screen based on which bottom button you tap
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (currentBottomTab == "Dashboard") {
                            DashboardScreen(viewModel = viewModel)
                        } else {
                            AdvisorScreen()
                        }
                    }
                }
            }

            AppScreen.MONTHLY_DASHBOARD -> {
                MonthlyDashboardScreen(
                    state = appState,
                    onBack = { appState.openDashboard() }
                )
            }

            AppScreen.ACCOUNT_DETAILS -> {
                AccountDetailsScreen(
                    state = appState,
                    onBack = { appState.openAssets() }
                )
            }

            AppScreen.ASSETS -> {
                AssetsScreen(
                    accounts = appState.accounts,
                    totalAssets = appState.totalAssets,
                    totalLiabilities = appState.totalLiabilities,
                    netWorth = appState.netWorth,
                    onAddAccount = { appState.openAddAccount() },
                    onAccountClick = { accountId -> appState.openAccountDetails(accountId) },
                    onBack = { appState.openDashboard() }
                )
            }

            AppScreen.ADD_ACCOUNT -> {
                // AddAccountScreen(state = appState, onBack = { appState.openAssets() })
            }

            AppScreen.ADD_EXPENSE -> {
                // AddExpenseScreen(state = appState, onBack = { appState.openDashboard() })
            }

            AppScreen.ADD_INCOME -> {
                // AddIncomeScreen(state = appState, onBack = { appState.openDashboard() })
            }
        }
    }
}