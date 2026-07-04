package com.financeos.app.models

data class Transaction(

    val id: Long = System.currentTimeMillis(),

    val amount: Double,

    val category: String,

    val notes: String,

    val type: TransactionType,

    val timestamp: Long = System.currentTimeMillis()

)

enum class TransactionType {

    EXPENSE,

    INCOME

}