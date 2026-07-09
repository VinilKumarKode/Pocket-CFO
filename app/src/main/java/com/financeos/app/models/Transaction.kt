package com.financeos.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double = 0.0,
    val type: String = "",
    val category: String = "",
    val isReconciled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val accountId: String = "",
    val date: Long = System.currentTimeMillis(),
    val paymentMethod: String = "",
    val rewardPointsEarned: Double = 0.0,
    val description: String = "",
    val sender: String = "Unknown",
    val rawMessage: String = "No original text saved."
)