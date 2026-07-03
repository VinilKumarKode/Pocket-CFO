package com.financeos.app.navigation

sealed class Screen(
    val route: String,
    val title: String
) {

    object Home : Screen(
        "home",
        "Home"
    )

    object Transactions : Screen(
        "transactions",
        "Transactions"
    )

    object Analytics : Screen(
        "analytics",
        "Analytics"
    )

    object More : Screen(
        "more",
        "More"
    )

}