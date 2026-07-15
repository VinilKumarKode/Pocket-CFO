package com.financeos.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.financeos.app.data.FinancialEntity
import com.financeos.app.data.Transaction

class PocketCFOState {

    // Core navigation states
    var currentScreen by mutableStateOf(AppScreen.DASHBOARD)
        private set

    var selectedAccountId by mutableStateOf<String?>(null)
        private set

    // Active state holders representing our DB tables in-memory
    var accounts by mutableStateOf<List<FinancialEntity>>(emptyList())
    var transactions by mutableStateOf<List<Transaction>>(emptyList())

    enum class AppScreen {
        DASHBOARD,
        MONTHLY_DASHBOARD,
        ACCOUNT_DETAILS,
        ASSETS,
        ADD_ACCOUNT,
        ADD_EXPENSE,
        ADD_INCOME
    }

    fun openDashboard() {
        currentScreen = AppScreen.DASHBOARD
    }

    fun openMonthlyDashboard() {
        currentScreen = AppScreen.MONTHLY_DASHBOARD
    }

    fun openAccountDetails(accountId: String) {
        selectedAccountId = accountId
        currentScreen = AppScreen.ACCOUNT_DETAILS
    }

    fun openAssets() {
        currentScreen = AppScreen.ASSETS
    }

    fun openAddExpense() {
        currentScreen = AppScreen.ADD_EXPENSE
    }
}