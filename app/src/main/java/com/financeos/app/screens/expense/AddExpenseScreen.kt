package com.financeos.app.screens.expense

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseScreen(

    onSave: (Double, String, String) -> Unit,

    onCancel: () -> Unit

) {

    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Top

    ) {

        Text(

            text = "Add Expense",

            style = MaterialTheme.typography.headlineMedium

        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(

            value = amount,

            onValueChange = {
                amount = it
            },

            label = {
                Text("Amount")
            },

            modifier = Modifier.fillMaxWidth()

        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(

            value = category,

            onValueChange = {
                category = it
            },

            label = {
                Text("Category")
            },

            modifier = Modifier.fillMaxWidth()

        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(

            value = notes,

            onValueChange = {
                notes = it
            },

            label = {
                Text("Notes")
            },

            modifier = Modifier.fillMaxWidth()

        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(

            modifier = Modifier.fillMaxWidth(),

            onClick = {

                val expenseAmount = amount.toDoubleOrNull() ?: 0.0

                onSave(
                    expenseAmount,
                    category,
                    notes
                )

            }

        ) {

            Text("Save")

        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(

            modifier = Modifier.fillMaxWidth(),

            onClick = onCancel

        ) {

            Text("Cancel")

        }

    }

}