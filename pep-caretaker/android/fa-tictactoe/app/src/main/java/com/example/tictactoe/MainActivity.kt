package com.example.tictactoe

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
<<<<<<< HEAD
import androidx.compose.animation.expandHorizontally
=======
>>>>>>> main
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
<<<<<<< HEAD
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect as LaunchedEffect1
=======
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
>>>>>>> main

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textToSpeech = TextToSpeech(this, this)

        setContent {
            val activity = LocalContext.current as Activity
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            TicTacToeScreen(textToSpeech)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.GERMAN
        }
    }

    override fun onDestroy() {
        textToSpeech.shutdown()
        super.onDestroy()
    }
}

@Composable
fun TicTacToeScreen(textToSpeech: TextToSpeech) {
<<<<<<< HEAD
    var playAgainstRobot by remember { mutableStateOf<Boolean?>(null) }

=======
>>>>>>> main
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("") }
    var namesSet by remember { mutableStateOf(false) }

    var player1Wins by remember { mutableStateOf(0) }
    var player2Wins by remember { mutableStateOf(0) }

    var board by remember { mutableStateOf(Array(3) { CharArray(3) { ' ' } }) }
    var currentPlayer by remember { mutableStateOf('X') }
    var winner by remember { mutableStateOf<Char?>(null) }
    var gameOver by remember { mutableStateOf(false) }
<<<<<<< HEAD
    var isRobotTurn by remember { mutableStateOf(false) }


=======
>>>>>>> main

    val backgroundColor = Color(0xFF1A1A1A)
    val fieldColor = Color(0xFFD32F2F)
    val textColor = Color(0xFFFFFFFF)

<<<<<<< HEAD
    if (playAgainstRobot == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .background(backgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Wählen Sie den Spiel modus",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { playAgainstRobot = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF388E3C)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Gegen einen Freund", color = textColor)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { playAgainstRobot = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Gegen den Roboter", color = textColor)
            }
        }


    } else if (!namesSet) {
=======
    if (!namesSet) {
>>>>>>> main
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
<<<<<<< HEAD
                text = if (playAgainstRobot == true) "Geben Sie Ihren Namen ein" else "Geben Sie die Spielernamen ein",
=======
                text = "Geben Sie die Spielernamen ein",
>>>>>>> main
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = player1Name,
                onValueChange = { player1Name = it },
<<<<<<< HEAD
                label = { Text(if (playAgainstRobot == true) "Ihr Name" else "Spieler 1 Name") },
=======
                label = { Text("Spieler 1 Name") },
>>>>>>> main
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White
                )
            )
<<<<<<< HEAD
            if (playAgainstRobot == false) {
                // Zweiter Spielername nur im Freundmodus
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = player2Name,
                    onValueChange = { player2Name = it },
                    label = { Text("Spieler 2 Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White
                    )
                )
            } else {

                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = "Pepper",
                    onValueChange = {},
                    label = { Text("Gegner: Pepper") },
                    enabled = false, // Eingabe deaktiviert
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Gray,
                        disabledTextColor = Color.Black,
                        disabledLabelColor = Color.DarkGray
                    )
                )
            }
=======

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = player2Name,
                onValueChange = { player2Name = it },
                label = { Text("Spieler 2 Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White
                )
            )

>>>>>>> main
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
<<<<<<< HEAD
                    if (player1Name.isNotEmpty() && (playAgainstRobot == false && player2Name.isNotEmpty() || playAgainstRobot == true)) {
                        if (playAgainstRobot == true) {
                            player2Name = "Pepper"
                        }
                        namesSet = true
                    } else {
                        textToSpeech.speak(
                            "Bitte geben Sie alle benötigten Namen ein",
=======
                    if (player1Name.isNotEmpty() && player2Name.isNotEmpty()) {
                        namesSet = true
                    } else {
                        textToSpeech.speak(
                            "Bitte geben Sie beide Spielernamen ein",
>>>>>>> main
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF388E3C)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Spiel Starten", color = textColor)
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tic-Tac-Toe",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Aktueller Spieler: ${if (currentPlayer == 'X') player1Name else player2Name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                for (i in 0..2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (j in 0..2) {
                            val playerSymbol = board[i][j]
                            val animatedFieldColor by animateColorAsState(
<<<<<<< HEAD
                                targetValue = when (playerSymbol) {
                                    'X' -> Color(0xFF388E3C) // Grün für Spieler 1 (X)
                                    'O' -> Color(0xFF1976D2)
                                    else -> fieldColor
                                },
=======
                                targetValue = if (playerSymbol == ' ') fieldColor else Color(0xFF388E3C),
>>>>>>> main
                                animationSpec = tween(durationMillis = 500)
                            )

                            Box(
                                modifier = Modifier
                                    .size(125.dp)
                                    .padding(6.dp)
                                    .background(animatedFieldColor, RoundedCornerShape(10.dp))
<<<<<<< HEAD
                                    .clickable(enabled = !gameOver && board[i][j] == ' ' && !isRobotTurn) {
                                        if (board[i][j] == ' ' && winner == null) {
=======
                                    .clickable(enabled = !gameOver) {
                                        if (playerSymbol == ' ' && winner == null) {
>>>>>>> main
                                            board[i][j] = currentPlayer
                                            winner = checkWinner(board)

                                            if (winner == null) {
                                                currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
<<<<<<< HEAD

                                                if (playAgainstRobot == true && currentPlayer == 'O') {
                                                    isRobotTurn = true
                                                }
=======
>>>>>>> main
                                            } else {
                                                gameOver = true
                                                if (winner == 'X') player1Wins++ else player2Wins++
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
<<<<<<< HEAD
                                    text = when (board[i][j]) {
=======
                                    text = when (playerSymbol) {
>>>>>>> main
                                        'X' -> "X"
                                        'O' -> "O"
                                        else -> ""
                                    },
                                    fontSize = 64.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }

<<<<<<< HEAD

=======
>>>>>>> main
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
<<<<<<< HEAD
// Roboterzug wird hier ausgeführt
                if (isRobotTurn) {
                    LaunchedEffect1(Unit) {
                        kotlinx.coroutines.delay(2000L) // 2 Sekunden warten
                        val robotMove = robotMove(board, 'O', 'X')
                        if (robotMove != null) {
                            board[robotMove.first][robotMove.second] = 'O'
                            winner = checkWinner(board)
                            if (winner == null) {
                                currentPlayer = 'X'
                            } else {
                                gameOver = true
                                if (winner == 'X') player1Wins++ else player2Wins++
                            }
                        }
                        isRobotTurn = false
                    }
                }


                if (gameOver && winner != null) {
=======

            if (gameOver && winner != null) {
>>>>>>> main
                val winnerName = if (winner == 'X') player1Name else player2Name

                // Full-Screen Winner Dialog
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Herzlichen Glückwunsch!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF388E3C)
                        )

                        Text(
                            text = "$winnerName hat gewonnen!",
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                resetGame {
                                    board = Array(3) { CharArray(3) { ' ' } }
                                    currentPlayer = 'X'
                                    winner = null
                                    gameOver = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF388E3C)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Neues Spiel", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

fun checkWinner(board: Array<CharArray>): Char? {
    for (i in 0..2) {
        if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != ' ') return board[i][0]
        if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != ' ') return board[0][i]
    }
    if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != ' ') return board[0][0]
    if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != ' ') return board[0][2]
    return null
}

fun resetGame(resetAction: () -> Unit) {
    resetAction()
<<<<<<< HEAD
}
fun robotMove(board: Array<CharArray>, robotSymbol: Char, playerSymbol: Char): Pair<Int, Int>? {
    // 1. Prüfen, ob der Roboter gewinnen kann
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j] == ' ') {
                board[i][j] = robotSymbol
                if (checkWinner(board) == robotSymbol) {
                    board[i][j] = ' ' // Rückgängig machen, da es nur eine Simulation ist
                    return Pair(i, j)
                }
                board[i][j] = ' ' // Rückgängig machen
            }
        }
    }

    // 2. Prüfen, ob der Roboter blockieren muss
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j] == ' ') {
                board[i][j] = playerSymbol
                if (checkWinner(board) == playerSymbol) {
                    board[i][j] = ' ' // Rückgängig machen
                    return Pair(i, j)
                }
                board[i][j] = ' ' // Rückgängig machen
            }
        }
    }

    // 3. Zufälligen Zug auswählen
    val emptyPositions = mutableListOf<Pair<Int, Int>>()
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j] == ' ') {
                emptyPositions.add(Pair(i, j))
            }
        }
    }

    return if (emptyPositions.isNotEmpty()) {
        emptyPositions.random()
    } else {
        null // Kein Zug möglich
    }
}
@Composable
fun RobotTurn(
    board: Array<CharArray>,
    onMoveComplete: (Pair<Int, Int>) -> Unit
) {
    LaunchedEffect1(Unit) {
        kotlinx.coroutines.delay(2000L) // Warte 2 Sekunden
        val robotMove = robotMove(board, 'O', 'X')
        if (robotMove != null) {
            onMoveComplete(robotMove)
        }
    }
}

=======
}
>>>>>>> main
