package com.financeos.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    Column(

        modifier = Modifier.padding(16.dp)

    ) {

        Text(

            text = "Pocket CFO",

            style = MaterialTheme.typography.headlineMedium

        )

        DashboardCard(

            title = "🧠 Financial Discovery",

            value = discoveryStatus,

            subtitle =
                """
Messages Read : $messagesRead

Financial SMS : $financialMessages

Banks Found : ${banks.size}

${banks.joinToString("\n")}
            """.trimIndent(),

            buttonText = "Start Discovery",

            onButtonClick = onDiscoveryClick

        )

        DashboardCard(

            title = "💳 Credit Cards",

            value = "${creditCards.size} Found",

            subtitle =
                if (creditCards.isEmpty()) {

                    "No credit cards discovered"

                } else {

                    creditCards.joinToString("\n") {

                        "${it.bank}  ****${it.last4}"

                    }

                }

        )

        DashboardCard(

            title = "Bills Due Today",

            value = "₹12,500",

            subtitle = "2 Bills Pending"

        )

        DashboardCard(

            title = "Cash Available",

            value = "₹3,42,500",

            subtitle = "Across 4 Bank Accounts"

        )

        DashboardCard(

            title = "💼 Assets",

            value = "Tap to Open",

            subtitle = "Manage your financial assets"

        )

        Text(

            text = "Open Assets →",

            modifier = Modifier
                .padding(top = 20.dp)
                .clickable {

                    onAssetsClick()

                }

        )

    }

}