package com.financeos.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

// Note: Ensure this import points to the new DashboardScreen we created.
// Depending on exactly where you saved it, it might be com.financeos.app.ui.screens.DashboardScreen
import com.financeos.app.ui.screens.DashboardScreen
import com.financeos.app.screens.MonthlyDashboardScreen
import com.financeos.app.screens.account.AccountDetailsScreen
import com.financeos.app.screens.assets.AssetsScreen
import com.financeos.app.state.PocketCFOState
import com.financeos.app.state.PocketCFOState.AppScreen
import com.financeos.app.viewmodel.FinanceViewModel

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
                // We pass the view model into our newly designed Command Center.
                // We can wire your onNavigate triggers back into the new UI later!
                DashboardScreen(
                    viewModel = viewModel
                )
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
                    onAddAccount = { appState.openAddAccount() }, // Connect the command here
                    onAccountClick = { accountId -> appState.openAccountDetails(accountId) },
                    onBack = { appState.openDashboard() }
                )
            }

            AppScreen.ADD_ACCOUNT -> {
                // Assuming you have an AddAccountScreen in your project
                // AddAccountScreen(state = appState, onBack = { appState.openAssets() })
            }

            AppScreen.ADD_EXPENSE -> {
                // Assuming you have an AddExpenseScreen in your project
                // AddExpenseScreen(state = appState, onBack = { appState.openDashboard() })
            }

            AppScreen.ADD_INCOME -> {
                // Assuming you have an AddIncomeScreen in your project
                // AddIncomeScreen(state = appState, onBack = { appState.openDashboard() })
            }
        }
    }
}