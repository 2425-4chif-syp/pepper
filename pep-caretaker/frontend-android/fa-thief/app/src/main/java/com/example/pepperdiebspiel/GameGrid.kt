package com.example.pepperdiebspiel

import android.media.MediaPlayer
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.min
import com.example.pepperdiebspiel.game.GameViewModel
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameGrid() {
    val gameViewModel: GameViewModel = viewModel()

    // Zugriff auf die Zustände aus dem ViewModel
    val gridItems by gameViewModel.gridItems
    val thiefPosition by gameViewModel.thiefPosition
    val gameWon by gameViewModel.gameWon
    val elapsedTime by gameViewModel.elapsedTime

    // Hier werden die Bilder definiert
    val images = listOf(
        R.drawable.water,
        R.drawable.church,
        R.drawable.sheep,
        R.drawable.witch,
        R.drawable.bird
    )

    // Anzeige, wenn das Spiel gewonnen wurde
    if (gameWon) {
        gameViewModel.stopTimer()

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFEB3B), Color(0xFFF57C00)),
                        radius = 600f
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.thief),
                    contentDescription = "Gefundener Dieb",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 24.dp)
                        .shadow(10.dp, shape = RectangleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Zeit benötigt: ${(elapsedTime / 1000)} Sekunden",
                    fontSize = 30.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.shadow(5.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Dieb wurde gefunden!",
                    fontSize = 36.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.shadow(5.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        gameViewModel.resetGame()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2)),
                    modifier = Modifier
                        .padding(8.dp)
                        .border(2.dp, Color.White)
                        .shadow(8.dp, shape = RectangleShape)
                ) {
                    Text("Neustart", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        gameViewModel.stopGame()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                    modifier = Modifier
                        .padding(8.dp)
                        .border(2.dp, Color.White)
                        .shadow(8.dp, shape = RectangleShape)
                ) {
                    Text("Beenden", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    } else {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        val cellSize = with(LocalDensity.current) {
            min(screenWidth / 8, screenHeight / 6) - 4.dp
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFB3E5FC), Color(0xFF81D4FA))
                    )
                )
        ) {
            LazyVerticalGrid(
                cells = GridCells.Fixed(8),
                modifier = Modifier.fillMaxSize()
            ) {
                items(gridItems.size) { index ->
                    GridItem(imageResId = images[gridItems[index]], size = cellSize) {
                        if (thiefPosition == index) {
                            gameViewModel.setGameWon(true)
                        }
                    }
                }
            }
        }
    }

    // Bewege den Dieb alle paar Sekunden
    LaunchedEffect(gameWon) {
        if (!gameWon) {
            while (isActive) {
                delay(4000)  // Warte 4 Sekunden für die Bewegung des Diebes
                gameViewModel.moveThief() // Bewege den Dieb
            }
        }
    }
}

@Composable
fun GridItem(imageResId: Int, size: Dp, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .padding(2.dp)
            .border(2.dp, Color.DarkGray)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                )
            )
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(4.dp),
            contentScale = ContentScale.Crop
        )
    }
}