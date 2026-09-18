package com.example.menu.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun EssensplanScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Titel der Seite
        Text(text = "Essensplan", modifier = Modifier.padding(bottom = 16.dp))

        // Button zum Zurücknavigieren
        Button(onClick = { navController.navigate("main_menu") }) {
            Text("Back to Menu")
        }
    }
}
