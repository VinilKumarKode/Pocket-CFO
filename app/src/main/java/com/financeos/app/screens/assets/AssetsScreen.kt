package com.financeos.app.screens.assets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.models.Account
import com.financeos.app.models.AccountType

@Composable
fun AssetsScreen(
    accounts: List<Account>,
    totalAssets: Double,
    totalLiabilities: Double,
    netWorth: Double,
    onAddAccount: () -> Unit, // Add this line
    onAccountClick: (String) -> Unit,
    onBack: () -> Unit
) {
    // ... rest of your code

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {

        Text(

            text = "Accounts",

            style = MaterialTheme.typography.headlineMedium

        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Total Assets : ₹%.2f".format(totalAssets)
                )

                Text(
                    "Liabilities : ₹%.2f".format(totalLiabilities)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Net Worth : ₹%.2f".format(netWorth),
                    style = MaterialTheme.typography.titleMedium
                )

            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onAddAccount
        ) {

            Text("Add Account")

        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            items(accounts) { account ->

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)

                ) {

                    Row(

                        verticalAlignment = Alignment.CenterVertically,

                        modifier = Modifier.padding(16.dp)

                    ) {

                        Icon(

                            imageVector = when (account.type) {

                                AccountType.BANK ->
                                    Icons.Default.AccountBalance

                                AccountType.CREDIT_CARD ->
                                    Icons.Default.CreditCard

                                AccountType.CASH ->
                                    Icons.Default.AttachMoney

                                AccountType.WALLET ->
                                    Icons.Default.AccountBalanceWallet

                                AccountType.UPI ->
                                    Icons.Default.Payments

                                else ->
                                    Icons.Default.TrendingUp

                            },

                            contentDescription = null

                        )

                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {

                            Text(
                                account.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                account.type.name.replace("_", " "),
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = "₹%.2f".format(account.balance),
                                style = MaterialTheme.typography.titleMedium
                            )

                            if (account.institution.isNotBlank()) {

                                Text(
                                    account.institution,
                                    style = MaterialTheme.typography.bodySmall
                                )

                            }

                        }

                    }

                }

            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(

            modifier = Modifier.fillMaxWidth(),

            onClick = onBack

        ) {

            Text("Back")

        }

    }

}