package com.example.memorygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memorygame.data.lokal.AppDatabase
import com.example.memorygame.data.repository.ScoreRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.memorygame.data.model.HighScoreItem
import com.example.memorygame.data.lokal.LocalPlayerScore

@Composable
fun HighScoresScreen(currentPlayerId: Long) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val playerScoreDao = db.playerScoreDao()

    val localScores by playerScoreDao.getAllScores().collectAsState(initial = emptyList())
    //var backendScores by remember { mutableStateOf<List<HighScoreItem>>(emptyList()) }
                                                                                            //Scores Mit Backend-Request
    /*LaunchedEffect(Unit) {
        val result = ScoreRepository.getScoresForPlayer(currentPlayerId)

        val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
        result.forEach {
            println("📥 Vollständiger Score vom Backend:\n${gson.toJson(it)}")
        }

        val mapped = result.map {
            val (rows, columns) = it.comment.split("x").let { parts ->
                if (parts.size == 2) parts.mapNotNull { p -> p.toIntOrNull() } else listOf(0, 0)
            }

            HighScoreItem(
                personId = it.person.id,
                firstName = it.person.firstName,
                lastName = it.person.lastName,
                gridRows = rows,
                gridColumns = columns,
                score = it.score,
                elapsedTime = it.elapsedTime,
                date = it.dateTime
            )
        }

        backendScores = mapped
    }*/

    // Beide Listen (Daten) in gemeinsamen Typ umwandeln:
    val combinedScores = (
            localScores.map { score: LocalPlayerScore ->
                val (rows, columns) = score.grid.split("x").let { parts ->
                    if (parts.size == 2) parts.map { p -> p.toIntOrNull() ?: 0 } else listOf(0, 0)
                }

                HighScoreItem(
                    personId = score.personId,
                    firstName = score.firstName,
                    lastName = score.lastName,
                    gridRows = rows,
                    gridColumns = columns,
                    score = score.score,
                    elapsedTime = score.elapsedTime,
                    date = score.date
                )
            } //+ backendScores
            ).sortedByDescending { it.date }



    val bestScore = combinedScores.maxByOrNull { it.score }

    val backgroundColor = Color.White
    val cardColor = Color(0xFF112D4E)
    val bestScoreColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor)
    ) {

        Text(
            text = "🏆 Dein Höchster Rekord",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF112D4E),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            elevation = 10.dp,
            shape = RoundedCornerShape(12.dp),
            backgroundColor = cardColor
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text("Name", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                    Text("Grid", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                    Text("Zeit", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                    Text("Punkte", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                }

                bestScore?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bestScoreColor)
                            .padding(8.dp)
                    ) {
                        Text("${it.firstName} ${it.lastName}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.Black)
                        Text("${it.gridRows}x${it.gridColumns}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.Black)
                        Text("${it.elapsedTime}s", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.Black)
                        Text("${it.score}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.Black)
                    }
                } ?: Text("Noch kein bester Score vorhanden.", color = Color.White)
            }
        }


        Text(
            text = "Deine zuletzt geschafften Punkte",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF112D4E),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text("Name", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color(0xFF112D4E))
                    Text("Grid", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color(0xFF112D4E))
                    Text("Zeit", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color(0xFF112D4E))
                    Text("Punkte", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color(0xFF112D4E))
                }
            }

            items(combinedScores) { score ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = cardColor
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text("${score.firstName} ${score.lastName}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.White)
                        Text("${score.gridRows}x${score.gridColumns}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.White)
                        Text("${score.elapsedTime}s", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.White)
                        Text("${score.score}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
