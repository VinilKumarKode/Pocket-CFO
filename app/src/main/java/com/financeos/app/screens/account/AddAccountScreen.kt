package com.financeos.app.screens.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
        String,
        String,
        Double,
        AccountType
    ) -> Unit,

    onCancel: () -> Unit

) {

    var name by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    var accountType by remember {
        mutableStateOf(AccountType.BANK)
    }

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

        Spacer(modifier = Modifier.height(20.dp))

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
            label = { Text("Institution") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = balance,
            onValueChange = { balance = it },
            label = { Text("Opening Balance") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = true }
        ) {

            Text("Type : ${accountType.name}")

        }

        DropdownMenu(

            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }

        ) {

            AccountType.entries.forEach {

                DropdownMenuItem(

                    text = {
                        Text(it.name.replace("_", " "))
                    },

                    onClick = {

                        accountType = it

                        expanded = false

                    }

                )

            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(

            modifier = Modifier.fillMaxWidth(),

            onClick = {

                onSave(

                    name,

                    institution,

                    balance.toDoubleOrNull() ?: 0.0,

                    accountType

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