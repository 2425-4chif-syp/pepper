package com.example.memorygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun GridSelectionScreen(navController: NavHostController) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF5F5DC),
            Color(0xFFFAF0E6),
            Color.White
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Wähle ein Grid zum Spielen 🎯",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    lineHeight = 36.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            listOf(
                "2x3 Kinderleicht 😜" to Pair(2, 3),
                "2x4 Hast du Erfahrung? 😉" to Pair(2, 4),
                "3x4 Profimodus 🎯" to Pair(3, 4),
                "4x4 Meister 🏆" to Pair(4, 4)
            ).forEach { (label, gridSize) ->
                Button(
                    onClick = {
                        navController.navigate("game_screen/${gridSize.first}/${gridSize.second}")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF112D4E)
                    )
                ) {
                    Text(
                        text = label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

