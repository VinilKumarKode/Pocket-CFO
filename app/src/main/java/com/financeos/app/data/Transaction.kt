package com.financeos.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String,
    val category: String,
    val date: Long,
    val paymentMethod: String,
    val rewardPointsEarned: Double?,
    val description: String,
    val isReconciled: Boolean = false
)