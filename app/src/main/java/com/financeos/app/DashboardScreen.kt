package com.financeos.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.components.DashboardCard
import com.financeos.app.models.CreditCard
import com.financeos.app.models.Transaction

@Composable
fun DashboardScreen(

    discoveryStatus: String,

    messagesRead: Int,

    financialMessages: Int,

    banks: List<String>,

    creditCards: List<CreditCard>,

    transactions: List<Transaction>,

    totalIncome: Double,

    totalExpense: Double,

    netWorth: Double,

    onAssetsClick: () -> Unit,

    onDiscoveryClick: () -> Unit,

    onAddExpenseClick: () -> Unit,

    onAddIncomeClick: () -> Unit

) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "Pocket CFO",
            style = MaterialTheme.typography.headlineMedium
        )

        DashboardCard(
            title = "Net Worth",
            value = "₹%.2f".format(netWorth),
            subtitle =
                """
Income : ₹%.2f

Expense : ₹%.2f
            """.trimIndent().format(
                    totalIncome,
                    totalExpense
                )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                modifier = Modifier.weight(1f),
                onClick = onAddExpenseClick
            ) {
                Text("+ Expense")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onAddIncomeClick
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
                onClick = onAssetsClick
            ) {
                Text("Accounts")
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
            title = "Recent Transactions",
            value =
                if (transactions.isEmpty())
                    "No transactions yet"
                else
                    "${transactions.size} Transactions",
            subtitle =
                if (transactions.isEmpty()) {
                    "Your latest expenses and income will appear here."
                } else {
                    transactions.take(5).joinToString("\n") {
                        "${it.type} • ₹${it.amount} • ${it.category}"
                    }
                }
        )

        Text(
            text = "Manage Accounts →",
            modifier = Modifier
                .padding(top = 20.dp)
                .clickable {
                    onAssetsClick()
                }
        )

    }

}