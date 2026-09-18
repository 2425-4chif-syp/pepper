package com.example.smartloginapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartloginapp.network.RetrofitInstance
import com.example.smartloginapp.presentation.LoginScreen
import com.example.smartloginapp.presentation.MainMenuScreen
import com.example.smartloginapp.ui.theme.SmartLoginAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import retrofit2.HttpException
import java.io.IOException
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartLoginAppTheme {
                // Navigation Controller erstellen
                val navController = rememberNavController()

                // NavHost für die Navigation
                NavHost(navController = navController, startDestination = "login") {
                    // Login Screen
                    composable("login") {
                        LoginScreen(
                            onLoginClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val response = RetrofitInstance.api.getUserName()
                                        if (response.isSuccessful) {
                                            val userName = response.body()?.name ?: "Unbekannt"
                                            // Navigiere zum MainMenuScreen nach erfolgreicher Anmeldung
                                            navController.navigate("mainMenu")
                                        } else {
                                            // Fehlerfall bei ungültiger Antwort
                                            Toast.makeText(
                                                applicationContext,
                                                "Fehler: ${response.code()}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: IOException) {
                                        // Netzwerkfehler
                                        Toast.makeText(
                                            applicationContext,
                                            "Netzwerkfehler, bitte versuchen Sie es später erneut.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: HttpException) {
                                        // Fehler vom Server
                                        Toast.makeText(
                                            applicationContext,
                                            "Serverfehler: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            onContinueWithoutLogin = {
                                // Weiter ohne Anmeldung
                                navController.navigate("mainMenu")
                            }
                        )
                    }

                    // Main Menu Screen
                    composable("mainMenu") {
                        MainMenuScreen()
                    }
                }
            }
        }
    }
}