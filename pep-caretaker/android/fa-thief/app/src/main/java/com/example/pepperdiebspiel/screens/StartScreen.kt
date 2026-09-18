package com.example.pepperdiebspiel.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pepperdiebspiel.R

@Composable
fun DifficultySelectionScreen(navController: NavController, onDifficultySelected: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6A5ACD), Color(0xFFB0C4DE))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Wähle den Schwierigkeitsgrad",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onDifficultySelected("easy")
                    navController.navigate("game")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                modifier = Modifier
                    .padding(8.dp)
                    .height(60.dp)
                    .width(200.dp)
                    .shadow(10.dp, RoundedCornerShape(30.dp))
            ) {
                Text(
                    text = "Einfach",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onDifficultySelected("medium")
                    navController.navigate("game")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFC107)),
                modifier = Modifier
                    .padding(8.dp)
                    .height(60.dp)
                    .width(200.dp)
                    .shadow(10.dp, RoundedCornerShape(30.dp))
            ) {
                Text(
                    text = "Mittel",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onDifficultySelected("hard")
                    navController.navigate("game")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336)),
                modifier = Modifier
                    .padding(8.dp)
                    .height(60.dp)
                    .width(200.dp)
                    .shadow(10.dp, RoundedCornerShape(30.dp))
            ) {
                Text(
                    text = "Schwer",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
