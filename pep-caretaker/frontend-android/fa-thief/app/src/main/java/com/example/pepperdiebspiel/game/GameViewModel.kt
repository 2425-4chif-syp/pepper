package com.example.pepperdiebspiel.game

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pepperdiebspiel.R
import kotlinx.coroutines.*
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val gridItems = mutableStateOf(List(48) { Random.nextInt(0, 5) })
    val thiefPosition = mutableStateOf(Random.nextInt(0, 48))
    val gameWon = mutableStateOf(false)
    val elapsedTime = mutableStateOf(0L)
    private var mediaPlayer: MediaPlayer? = null
    private var isTimerRunning = mutableStateOf(true)

    val images = listOf(
        R.drawable.water,
        R.drawable.church,
        R.drawable.sheep,
        R.drawable.witch,
        R.drawable.bird
    )

    val sounds = listOf(
        R.raw.water_sound,
        R.raw.church_bells,
        R.raw.sheep_bleat,
        R.raw.witch_laugh,
        R.raw.bird_chirp
    )

    init {
        startTimer()
    }

    fun startTimer() {
        viewModelScope.launch(Dispatchers.Main) {
            while (!gameWon.value) {
                delay(1000)
                elapsedTime.value += 1000
            }
        }
    }

    // Stoppt den Timer
    fun stopTimer() {
        isTimerRunning.value = false
    }

    // Setzt das Spiel zurück
    fun resetGame() {
        gridItems.value = List(48) { Random.nextInt(0, images.size) }
        thiefPosition.value = (0 until 48).random()
        gameWon.value = false
        elapsedTime.value = 0L
        isTimerRunning.value = true
        startTimer()
    }


    // Stoppt das Spiel
    fun stopGame() {
        stopTimer()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("GameViewModel", "Game beendet.")
    }

    fun setGameWon(won: Boolean) {
        gameWon.value = won
    }

    // Logik für die Bewegung des Diebes
    fun moveThief(): Int {
        val possibleMoves = mutableListOf<Int>()
        if (thiefPosition.value % 8 != 0) possibleMoves.add(thiefPosition.value - 1)
        if (thiefPosition.value % 8 != 7) possibleMoves.add(thiefPosition.value + 1)
        if (thiefPosition.value >= 8) possibleMoves.add(thiefPosition.value - 8)
        if (thiefPosition.value < 40) possibleMoves.add(thiefPosition.value + 8)
        thiefPosition.value = possibleMoves.random() // Zufällige Bewegung des Diebes

        // Berechne die Zeile und Spalte basierend auf der Position des Diebes (1-basiert)
        val row = thiefPosition.value / 8 + 1 // Zeile 1-basiert
        val column = thiefPosition.value % 8 + 1 // Spalte 1-basiert

        // Logge die Position des Diebes
        Log.d("GameViewModel", "Der Dieb befindet sich in Grid [$row][$column]")

        // Sofort den Sound des neuen Grid abspielen
        viewModelScope.launch {
            playSound(thiefPosition.value) // Spiele den entsprechenden Sound ab
        }

        return thiefPosition.value
    }

    // Methode zum Abspielen von Sounds
    suspend fun playSound(thiefPosition: Int) {
        try {
            val imageIndex = gridItems.value[thiefPosition]
            val soundResId = sounds[imageIndex]

            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }

            mediaPlayer = MediaPlayer.create(getApplication(), soundResId)
            mediaPlayer?.setOnCompletionListener {
                mediaPlayer?.release() // Release nach Beendigung
                mediaPlayer = null
            }
            mediaPlayer?.start() // Starte den neuen Sound
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error playing sound: ${e.message}")
        }
    }
}
