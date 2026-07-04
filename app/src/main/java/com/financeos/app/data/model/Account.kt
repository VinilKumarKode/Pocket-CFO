package com.financeos.app.data.model

import java.util.UUID

data class Account(

    val id: String = UUID.randomUUID().toString(),

    val name: String,

    val type: AccountType,

    val balance: Double,

    val currency: String = "INR",

    val bankName: String = "",

    val accountNumber: String = "",

    val creditLimit: Double = 0.0,

    val statementDay: Int = 1,

    val dueDay: Int = 1,

    val color: Long = 0xFF2196F3,

    val icon: String = "account_balance",

    val isArchived: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()

)