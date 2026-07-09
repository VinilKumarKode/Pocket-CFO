package com.financeos.app.models

enum class TransactionType {
    EXPENSE, INCOME, TRANSFER
}

object TransactionCategories {
    val EXPENSE_CATEGORIES = listOf("Food", "Transport", "Shopping", "Bills", "Groceries", "Other")
    val INCOME_CATEGORIES = listOf("Salary", "Freelance", "Investment", "Other")
}