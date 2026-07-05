package com.financeos.app.models

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: String,
    val notes: String,
    val type: TransactionType,
    val timestamp: Long = System.currentTimeMillis(), // Using Long instead of LocalDateTime
    val accountId: String? = null
)

enum class TransactionType {
    INCOME, EXPENSE
}

object TransactionCategories {
    val EXPENSE_CATEGORIES = listOf("Food", "Fuel", "Shopping", "Medical", "Travel", "Bills", "Other")
    val INCOME_CATEGORIES = listOf("Salary", "Investment", "Bonus", "Other")
}