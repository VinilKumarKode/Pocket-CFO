package com.financeos.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavHostController
) {

    val items = listOf(
        Screen.Home,
        Screen.Transactions,
        Screen.Analytics,
        Screen.More
    )

    NavigationBar {

        val currentDestination =
            navController.currentBackStackEntryAsState().value?.destination

        items.forEach { screen ->

            NavigationBarItem(

                selected = currentDestination?.route == screen.route,

                onClick = {

                    navController.navigate(screen.route) {

                        launchSingleTop = true

                        restoreState = true

                        popUpTo(navController.graph.startDestinationId) {

                            saveState = true

                        }

                    }

                },

                icon = {

                    when (screen) {

                        Screen.Home ->
                            Icon(Icons.Default.Home, null)

                        Screen.Transactions ->
                            Icon(Icons.Default.SwapHoriz, null)

                        Screen.Analytics ->
                            Icon(Icons.Default.Analytics, null)

                        Screen.More ->
                            Icon(Icons.Default.Menu, null)

                    }

                },

                label = {

                    Text(screen.title)

                }

            )

        }

    }

}