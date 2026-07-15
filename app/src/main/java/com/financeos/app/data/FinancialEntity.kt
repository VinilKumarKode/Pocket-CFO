package com.financeos.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "financial_entities",
    indices = [Index(value = ["name", "lastFourDigits"], unique = true)] // ENFORCES UNIQUE CARDS
)
data class FinancialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val lastFourDigits: String,
    val balance: Double,
    val creditLimit: Double? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)