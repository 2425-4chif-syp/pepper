package com.example.pepperdiebspiel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.pepperdiebspiel.navigation.AppNavigation
import com.example.pepperdiebspiel.ui.theme.PepperDiebSpielTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PepperDiebSpielTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    var selectedDifficulty by remember { mutableStateOf("easy") }

                    AppNavigation(
                        navController = navController,
                        onDifficultySelected = { difficulty ->
                            selectedDifficulty = difficulty
                        }
                    )
                }
            }
        }
    }
}
