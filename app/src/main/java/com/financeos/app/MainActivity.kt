package com.financeos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.financeos.app.ui.theme.FinanceOSTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            FinanceOSTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    WelcomeScreen()

                }

            }

        }

    }

}

@Composable
fun WelcomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {

        Text(
            text = "💼",
            fontSize = 60.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Pocket CFO",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Know. Plan. Grow.",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Welcome to your personal financial assistant.",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Version 0.1 - Genesis",
            fontSize = 14.sp
        )

    }

}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {

    FinanceOSTheme {

        WelcomeScreen()

    }

}