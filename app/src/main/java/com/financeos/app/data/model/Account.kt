package com.financeos.app.data.model

data class Account(

    val id: Long = 0,

    val name: String,

    val type: AccountType,

    val balance: Double = 0.0,

    val bankName: String? = null,

    val accountNumber: String? = null,

    val isActive: Boolean = true

)