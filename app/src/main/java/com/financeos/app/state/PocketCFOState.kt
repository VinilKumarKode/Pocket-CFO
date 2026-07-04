package com.financeos.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.financeos.app.models.CreditCard

class PocketCFOState {

    enum class AppScreen {
        DASHBOARD,
        ASSETS,
        ADD_EXPENSE
    }

    var currentScreen by mutableStateOf(AppScreen.DASHBOARD)
        private set

    var discoveryStatus by mutableStateOf("Ready")
        private set

    var messagesRead by mutableIntStateOf(0)
        private set

    var financialMessagesFound by mutableIntStateOf(0)
        private set

    var banks by mutableStateOf<List<String>>(emptyList())
        private set

    var creditCards by mutableStateOf<List<CreditCard>>(emptyList())
        private set

    fun openDashboard() {
        currentScreen = AppScreen.DASHBOARD
    }

    fun openAssets() {
        currentScreen = AppScreen.ASSETS
    }

    fun openAddExpense() {
        currentScreen = AppScreen.ADD_EXPENSE
    }

    fun startDiscovery() {
        discoveryStatus = "Scanning..."
    }

    fun discoveryCompleted(
        totalMessages: Int,
        financialMessages: Int,
        banksFound: List<String>,
        cardsFound: List<CreditCard>
    ) {

        messagesRead = totalMessages
        financialMessagesFound = financialMessages
        banks = banksFound
        creditCards = cardsFound

        discoveryStatus = "Completed"
    }
}