package com.financeos.app.model

data class BankAccount(

    val id: Int,

    val bankName: String,

    val accountType: String,

    val isPrimary: Boolean,

    val currentBalance: Double = 0.0,

    val accountNickname: String = "",

    val isActive: Boolean = true

)

