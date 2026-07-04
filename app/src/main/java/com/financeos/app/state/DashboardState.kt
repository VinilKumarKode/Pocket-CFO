package com.financeos.app.state

data class DashboardState(

    val billsDueToday: String = "--",

    val creditCardDue: String = "--",

    val cashAvailable: String = "--",

    val discoveryStatus: String = "Ready to discover"

)

