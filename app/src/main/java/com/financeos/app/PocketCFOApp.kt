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
import com.financeos.app.state.PocketCFOState

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

        if (appState.showAssets) {

            AssetsScreen()

        } else {

            DashboardScreen(

                discoveryStatus = appState.discoveryStatus,

                messagesRead = appState.messagesRead,

                financialMessages = appState.financialMessagesFound,

                banks = appState.banks,

                creditCards = appState.creditCards,

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

                }

            )

        }

    }

}