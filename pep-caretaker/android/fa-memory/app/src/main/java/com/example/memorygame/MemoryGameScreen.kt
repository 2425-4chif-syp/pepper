package com.example.memorygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.memorygame.data.lokal.AppDatabase
import com.example.memorygame.data.lokal.LocalPlayerScore
import com.example.memorygame.data.model.Game
import com.example.memorygame.data.model.GameType
import com.example.memorygame.data.model.Person
import com.example.memorygame.data.model.PersonIntent
import com.example.memorygame.data.remote.PersonApi
import com.example.memorygame.data.repository.ScoreRepository
import com.example.memorygame.data.remote.ScoreRequest
import com.example.memorygame.logic.ScoreManager
import com.example.memorygame.logic.image.GameImageProvider
import com.example.memorygame.logic.image.ImageData
import com.example.memorygame.logic.image.ImageDecoder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.memorygame.ui.dialogs.WinDialog
import com.example.memorygame.logic.restartGame
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Composable
fun MemoryGameScreen(navController: NavHostController, rows: Int, columns: Int, personIntent: PersonIntent?, personId: Long?, personApi: PersonApi) {

    val scoreManager = remember { ScoreManager(rows, columns) }
    val gameLogic = remember { GameLogic(scoreManager) }

    val effectivePersonId: Long = personId ?: personIntent?.id ?: -1L

    /*val selectedImages = cardImages.shuffled().take((rows * columns) / 2)
    val cards = remember {
        mutableStateListOf(*selectedImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }.shuffled().toTypedArray())
    }*/

    val neededPairs = (rows * columns) / 2
    var imageList by remember { mutableStateOf<List<ImageData>>(emptyList()) }

    val cards = remember {
        mutableStateListOf<MemoryCard>()
    }

    LaunchedEffect(personId) {
        val images = GameImageProvider.getImages(
            neededPairs = neededPairs,
            personId = effectivePersonId,
            api = personApi
        )

        val newCards = images
            .flatMap { image -> listOf(MemoryCard(image.hashCode(), image), MemoryCard(image.hashCode(), image)) }
            .shuffled()

        cards.clear()
        cards.addAll(newCards)
    }


    val flippedCards by remember { derivedStateOf { gameLogic.flippedCards } }
    val matchedCards by remember { derivedStateOf { gameLogic.matchedCards } }
    val isGameOver by remember { derivedStateOf { gameLogic.isGameOver } }
    val coroutineScope = rememberCoroutineScope()

    var gameStartTime by remember { mutableStateOf(0L) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val gridAvailableHeight = screenHeight - 70.dp
    val cardWidth = screenWidth / columns
    val cardHeight = gridAvailableHeight / rows

    // Timer starten
    LaunchedEffect(isGameOver) {
        if (!gameLogic.isGameOver) {
            elapsedSeconds = 0
            gameStartTime = System.currentTimeMillis()
            while (!gameLogic.isGameOver) {
                delay(1000L)
                elapsedSeconds++
            }
            val totalGameTimeSeconds = ((System.currentTimeMillis() - gameStartTime) / 1000).toInt()
            scoreManager.applyTimeBonus(totalGameTimeSeconds, rows, columns)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.jungle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (isGameOver) {
            WinDialog(
                onRestart = {
                    coroutineScope.launch {
                        restartGame(
                            cards = cards,
                            matchedCards = matchedCards,
                            flippedCards = flippedCards,
                            rows = rows,
                            columns = columns,
                            scoreManager = scoreManager,
                            personId = effectivePersonId,
                            personApi = personApi
                        )
                        gameLogic.isGameOver = false
                    }
                },
                onGoToMainMenu = {
                    navController.navigate("main_menu")
                },
                elapsedSeconds = elapsedSeconds,
                scoreManager = scoreManager
            )
        }


        val context = LocalContext.current
        val db = AppDatabase.getInstance(context)
        val playerScoreDao = db.playerScoreDao()
        //val repository = remember { ScoreRepository() }


        if (isGameOver) {
            LaunchedEffect(Unit) {
                // Scores mit Backend-Request
                /*val scoreRequest = createScoreRequest(
                    score = scoreManager.currentScore,
                    elapsedTime = elapsedSeconds,
                    comment = "${rows}x${columns}",
                    person = Person( // Dummy-Daten, später dynamisch laden
                        id = 1L,
                        firstName = "Anna",
                        lastName = "Beispiel",
                        dob = "1970-01-01",
                        gender = false,
                        isWorker = false,
                        roomNo = "123"
                    ),
                    game = Game(
                        id = 1L,
                        name = "Memory für Anna",
                        enabled = true,
                        gameType = GameType(
                            id = "MEMORY",
                            name = "Memory"
                        )
                    )
                )

                val success = ScoreRepository.sendScore(scoreRequest)
                if (success) println("🎉 Score gespeichert")
                else println("⚠️ Fehler beim Speichern")*/

                val newScore = LocalPlayerScore(
                    personId = effectivePersonId,
                    firstName = personIntent?.firstName ?: "Unbekannt",
                    lastName = personIntent?.lastName ?: "Lastname",
                    grid = "${rows}x${columns}",
                    score = scoreManager.currentScore,
                    elapsedTime = elapsedSeconds,
                    date = getCurrentDateTimeString()
                )

                playerScoreDao.insertScore(newScore)

            }
        }


        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(cards.size) { index ->
                val card = cards[index]
                val isFlipped = card.isFlipped || index in matchedCards
                val borderColor = if (index in flippedCards) Color.Green else Color(0x80FFFFFF)
                val borderWidth = if (index in flippedCards) 3.dp else 2.dp

                Box(
                    modifier = Modifier
                        .width(cardWidth)
                        .height(cardHeight)
                        .padding(4.dp)
                        .background(Color(0x80FFFFFF))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(enabled = !isFlipped) {
                            if (flippedCards.size < 2 && index !in matchedCards) {
                                coroutineScope.launch {
                                    gameLogic.flipCard(index, cards)
                                }
                            }
                        }
                        .border(borderWidth, borderColor, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    /*if (isFlipped) {
                        Image(
                            painter = painterResource(id = card.image),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent),
                            contentScale = ContentScale.Fit
                        )
                    }*/
                    if (isFlipped) {
                        when (val image = card.image) {
                            is ImageData.DrawableImage -> {
                                Image(
                                    painter = painterResource(id = image.resId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            is ImageData.Base64Image -> {
                                val imageBitmap = remember(image.base64) {
                                    ImageDecoder.decodeBase64ToImageBitmap(image.base64)
                                }
                                if (imageBitmap != null) {
                                    println("Bild erfolgreich decodiert")
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Transparent),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    println("Base64 konnte nicht decodiert werden.")
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Gray)
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            contentAlignment = Alignment.Center,

                            ) {
                            Text(
                                text = "🔍 Karte verdeckt",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val formattedTime = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)
            Text(
                text = "Deine Zeit: $formattedTime",
                color = Color.Red,
                fontSize = 18.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(4f, 10f),
                        blurRadius = 1f
                    )
                )
            )
            Text(
                text = "Punkte: ${scoreManager.currentScore}",
                color = Color.Red,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(4f, 10f),
                        blurRadius = 1f
                    )
                )
            )
        }
    }
}

fun createScoreRequest(
    score: Int,
    elapsedTime: Int,
    comment: String,
    person: Person,
    game: Game
): ScoreRequest {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val formattedDateTime = formatter.format(Date())

    return ScoreRequest(
        score = score,
        elapsedTime = elapsedTime,
        comment = comment,
        person = person,
        game = game,
        dateTime = formattedDateTime
    )
}

fun getCurrentDateTimeString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}

