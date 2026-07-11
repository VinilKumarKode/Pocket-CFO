package com.financeos.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_entities")
data class FinancialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,             // e.g., "HDFC Bank", "SBI Cashback"
    val type: String,             // e.g., "BANK_ACCOUNT", "CREDIT_CARD", "LOAN", "FD"
    val lastFourDigits: String? = null,
    var balance: Double = 0.0,
    val creditLimit: Double? = null
)