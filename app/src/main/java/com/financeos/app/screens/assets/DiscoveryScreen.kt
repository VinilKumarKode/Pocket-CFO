package com.financeos.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DiscoveryScreen(
    message: String,
    onDiscoverClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {

        Text(
            text = "Welcome to Pocket CFO",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Let's discover your financial world.",
            modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
        )

        Button(
            onClick = onDiscoverClick
        ) {

            Text("Discover My Financial Life")

        }

        Text(
            text = message,
            modifier = Modifier.padding(top = 32.dp)
        )

    }

}

