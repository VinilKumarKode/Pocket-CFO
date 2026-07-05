package com.financeos.app.screens.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeos.app.models.AccountType

@Composable
fun AddAccountScreen(

    onSave: (
        name: String,
        institution: String,
        balance: Double,
        type: AccountType
    ) -> Unit,

    onCancel: () -> Unit

) {

    var name by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Top

    ) {

        Text(
            "Add Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Account Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = institution,
            onValueChange = { institution = it },
            label = { Text("Bank / Institution") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = balance,
            onValueChange = { balance = it },
            label = { Text("Opening Balance") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(

            modifier = Modifier.fillMaxWidth(),

            onClick = {

                onSave(

                    name,

                    institution,

                    balance.toDoubleOrNull() ?: 0.0,

                    AccountType.BANK

                )

            }

        ) {

            Text("Save")

        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(

            modifier = Modifier.fillMaxWidth(),

            onClick = onCancel

        ) {

            Text("Cancel")

        }

    }

}