package com.financeos.app.models

data class DiscoveryResult(

    val messagesRead: Int,

    val financialMessages: Int,

    val banks: List<String>,

    val creditCards: List<CreditCard>

)