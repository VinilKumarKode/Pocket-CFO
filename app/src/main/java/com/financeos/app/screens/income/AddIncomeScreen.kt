package com.financeos.app.screens.income

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun AddIncomeScreen(

    onSave: (Double, String, String) -> Unit,

    onCancel: () -> Unit

) {

    var amount by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Top

    ) {

        Text(
            text = "Add Income",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = source,
            onValueChange = { source = it },
            label = { Text("Source") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(

            modifier = Modifier.fillMaxWidth(),

            onClick = {

                onSave(

                    amount.toDoubleOrNull() ?: 0.0,

                    source,

                    notes

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