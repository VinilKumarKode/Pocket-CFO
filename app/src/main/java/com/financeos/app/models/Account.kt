package com.financeos.app.models

enum class AccountType {

    BANK,
    CREDIT_CARD,
    CASH,
    WALLET,
    UPI,
    FIXED_DEPOSIT,
    PPF,
    STOCK,
    MUTUAL_FUND,
    GOLD,
    LOAN

}

data class Account(

    val id: Long = System.currentTimeMillis(),

    val name: String,

    val type: AccountType,

    val balance: Double,

    val institution: String = ""

) {

    val isLiability: Boolean

        get() = type == AccountType.LOAN ||
                type == AccountType.CREDIT_CARD

}