package com.financeos.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["sender", "amount", "date"], unique = true)] // ENFORCES UNIQUE TRANSACTIONS
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String,             // "INCOME" or "EXPENSE"
    val category: String,
    val date: Long,
    val paymentMethod: String,
    val description: String,
    val isReconciled: Boolean = false,
    val sender: String,
    val rawMessage: String = ""
)