package com.financeos.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.financeos.app.models.CreditCard

class PocketCFOState {

    var showAssets by mutableStateOf(false)
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

    fun openAssets() {
        showAssets = true
    }

    fun closeAssets() {
        showAssets = false
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