package com.financeos.app.models

import java.time.LocalDateTime
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: String,
    val notes: String,
    val type: TransactionType,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val accountId: String? = null // This links the transaction to a specific account
)

enum class TransactionType {
    INCOME, EXPENSE
}