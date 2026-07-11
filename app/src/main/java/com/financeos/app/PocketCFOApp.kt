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
import com.financeos.app.screens.income.MonthlyDashboardScreen
import com.financeos.app.screens.account.AccountDetailsScreen
import com.financeos.app.screens.assets.AssetsScreen
import com.financeos.app.screens.expense.AddExpenseScreen
import com.financeos.app.screens.SplashScreen // <-- We imported your new Splash Screen!
import com.financeos.app.state.PocketCFOState
import com.financeos.app.state.PocketCFOState.AppScreen
import com.financeos.app.viewmodel.FinanceViewModel
import com.financeos.app.ui.screens.accounts.AdvisorScreen

@Composable
fun PocketCFOApp(viewModel: FinanceViewModel) {
    // This acts as a gatekeeper. It starts as 'true' so the splash screen shows first.
    var showSplash by remember { mutableStateOf(true) }

    val appState = remember { PocketCFOState() }
    val context = LocalContext.current

    // THE GATEKEEPER LOGIC
    if (showSplash) {
        // Show the Splash Screen. When it finishes its 2-second timer, it sets showSplash to false.
        SplashScreen(onSplashComplete = { showSplash = false })
    } else {
        // Once showSplash is false, draw the actual app!
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (appState.currentScreen) {
                AppScreen.DASHBOARD -> {
                    var currentBottomTab by remember { mutableStateOf("Dashboard") }

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
                        Box(modifier = Modifier.padding(innerPadding)) {
                            if (currentBottomTab == "Dashboard") {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToAnalytics = { appState.openMonthlyDashboard() },
                                    onNavigateToAddExpense = { appState.openAddExpense() }
                                )
                            } else {
                                AdvisorScreen()
                            }
                        }
                    }
                }

                AppScreen.MONTHLY_DASHBOARD -> {
                    MonthlyDashboardScreen(
                        viewModel = viewModel,
                        state = appState
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

                AppScreen.ADD_ACCOUNT -> {}

                AppScreen.ADD_EXPENSE -> {
                    AddExpenseScreen(onBack = { appState.openDashboard() })
                }

                AppScreen.ADD_INCOME -> {}
            }
        }
    }
}