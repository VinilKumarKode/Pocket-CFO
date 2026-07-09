package com.financeos.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.financeos.app.data.Transaction

@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit
) {
    // FIXED: Checking against the "INCOME" string
    val isIncome = transaction.type == "INCOME"
    val amountColor = if (isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val prefix = if (isIncome) "+" else "-"
    val badgeContainerColor = if (isIncome) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.category, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                // FIXED: Requesting 'description' instead of 'notes' from the database
                if (transaction.description.isNotBlank()) {
                    Text(text = transaction.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$prefix₹${"%.2f".format(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
        }
    }
}