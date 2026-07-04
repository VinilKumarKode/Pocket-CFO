package com.financeos.app.screens.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseScreen(

    onSave: () -> Unit,

    onCancel: () -> Unit

) {

    val amount = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }

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

            value = amount.value,

            onValueChange = {
                amount.value = it
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

            value = category.value,

            onValueChange = {
                category.value = it
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

            value = notes.value,

            onValueChange = {
                notes.value = it
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

            onClick = onSave,

            modifier = Modifier.fillMaxWidth()

        ) {

            Text("Save")

        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(

            onClick = onCancel,

            modifier = Modifier.fillMaxWidth()

        ) {

            Text("Cancel")

        }

    }

}