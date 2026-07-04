package com.financeos.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardCard(
    title: String,
    value: String,
    subtitle: String = "",
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )

            if (subtitle.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium
                )

            }

            if (buttonText != null && onButtonClick != null) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onButtonClick
                ) {

                    Text(buttonText)

                }

            }

        }

    }

}