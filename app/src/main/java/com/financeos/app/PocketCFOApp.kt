package com.financeos.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.financeos.app.discovery.DiscoveryEngine
import com.financeos.app.screens.account.AddAccountScreen
import com.financeos.app.screens.assets.AssetsScreen
import com.financeos.app.screens.expense.AddExpenseScreen
import com.financeos.app.screens.income.AddIncomeScreen
import com.financeos.app.state.PocketCFOState
import com.financeos.app.state.PocketCFOState.AppScreen

@Composable
fun PocketCFOApp() {

    val appState = remember { PocketCFOState() }

    val context = LocalContext.current

    Surface(

        modifier = Modifier.fillMaxSize(),

        color = MaterialTheme.colorScheme.background

    ) {

        when (appState.currentScreen) {
            AppScreen.ACCOUNT_DETAILS -> {
                com.financeos.app.screens.account.AccountDetailsScreen(
                    state = appState,
                    onBack = { appState.openAssets() }
                )
            }
            AppScreen.DASHBOARD -> {
                DashboardScreen(
                    state = appState,
                    onNavigateToExpense = { appState.openAddExpense() },
                    onNavigateToIncome = { appState.openAddIncome() },
                    onNavigateToAssets = { appState.openAssets() }
                )
            }
            AppScreen.ASSETS -> {

                AssetsScreen(

                    accounts = appState.accounts,

                    totalAssets = appState.totalAssets,

                    totalLiabilities = appState.totalLiabilities,

                    netWorth = appState.netWorth,

                    onAddAccount = {

                        appState.openAddAccount()

                    },

                    onBack = {

                        appState.openDashboard()

                    }

                )

            }

            AppScreen.ADD_ACCOUNT -> {

                AddAccountScreen(

                    onSave = { name, institution, balance, accountType ->

                        appState.addAccount(

                            name = name,

                            type = accountType,

                            balance = balance,

                            institution = institution

                        )

                        appState.openAssets()

                    },

                    onCancel = {

                        appState.openAssets()

                    }

                )

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

            AppScreen.ADD_INCOME -> {

                AddIncomeScreen(

                    onSave = { amount, source, notes ->

                        appState.addIncome(

                            amount,

                            source,

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