package com.financeos.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.financeos.app.navigation.BottomBar
import com.financeos.app.navigation.PocketCFONavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketCFOApp() {

    val navController = rememberNavController()

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text("Pocket CFO")

                }

            )

        },

        bottomBar = {

            BottomBar(navController)

        }

    ) { innerPadding ->

        PocketCFONavGraph(

            navController = navController,

            modifier = Modifier.padding(innerPadding)

        )

    }

}