package com.financeos.app

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.financeos.app.components.DashboardCard
import com.financeos.app.models.CreditCard

@Composable
fun DashboardScreen(

    discoveryStatus: String,

    messagesRead: Int,

    financialMessages: Int,

    banks: List<String>,

    creditCards: List<CreditCard>,

    onAssetsClick: () -> Unit,

    onDiscoveryClick: () -> Unit

) {

    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "Pocket CFO",
            style = MaterialTheme.typography.headlineMedium
        )

        DashboardCard(
            title = "Net Worth",
            value = "₹0.00",
            subtitle = "Assets - Liabilities"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    Toast.makeText(
                        context,
                        "Add Expense - Coming Soon",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {
                Text("+ Expense")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    Toast.makeText(
                        context,
                        "Add Income - Coming Soon",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {
                Text("+ Income")
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    Toast.makeText(
                        context,
                        "Add Account - Coming Soon",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {
                Text("+ Account")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onDiscoveryClick
            ) {
                Text("Discovery")
            }

        }

        DashboardCard(

            title = "Financial Discovery",

            value = discoveryStatus,

            subtitle =
                """
Messages Read : $messagesRead

Financial SMS : $financialMessages

Banks Found : ${banks.size}

Credit Cards : ${creditCards.size}
            """.trimIndent()

        )

        DashboardCard(
            title = "Accounts",
            value = "Tap to Open",
            subtitle = "Banks • Wallets • Cards • Investments"
        )

        Text(
            text = "Manage Accounts →",
            modifier = Modifier
                .padding(top = 20.dp)
                .clickable {
                    onAssetsClick()
                }
        )

        DashboardCard(
            title = "Recent Transactions",
            value = "No transactions yet",
            subtitle = "Your latest expenses and income will appear here."
        )

    }

}