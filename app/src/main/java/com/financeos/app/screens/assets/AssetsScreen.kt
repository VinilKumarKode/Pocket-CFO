package com.financeos.app.screens.assets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.models.Account

@Composable
fun AssetsScreen(

    accounts: List<Account>,

    onAddAccount: () -> Unit,

    onBack: () -> Unit

) {

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

        Button(

            modifier = Modifier.fillMaxWidth(),

            onClick = onAddAccount

        ) {

            Text("Add Account")

        }

        Spacer(modifier = Modifier.height(16.dp))

        if (accounts.isEmpty()) {

            Text("No accounts added yet.")

        } else {

            LazyColumn {

                items(accounts) { account ->

                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)

                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                account.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(account.type.name)

                            Text("₹%.2f".format(account.balance))

                            if (account.institution.isNotBlank()) {

                                Text(account.institution)

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