package com.financeos.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun PocketCFONavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {

        composable(Screen.Home.route) {
            PlaceholderScreen("Home")
        }

        composable(Screen.Transactions.route) {
            PlaceholderScreen("Transactions")
        }

        composable(Screen.Analytics.route) {
            PlaceholderScreen("Analytics")
        }

        composable(Screen.More.route) {
            PlaceholderScreen("More")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(title)

    }

}