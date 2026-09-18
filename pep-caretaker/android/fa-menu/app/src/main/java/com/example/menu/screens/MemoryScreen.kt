package com.example.menu.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MemoryScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Titel der Seite
        Text(
            text = "Memory Game",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Button zum Zurücknavigieren
        Button(
            onClick = {
                navController.navigate("main_menu") // Navigation zurück zum Hauptmenü
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back to Menu")
        }
    }
}
