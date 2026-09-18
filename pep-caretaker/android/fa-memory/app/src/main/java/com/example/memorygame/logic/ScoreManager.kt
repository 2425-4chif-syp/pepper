package com.example.memorygame.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class ScoreManager(rows: Int, columns: Int) {

    private val pointsPerMatch: Int
    private val pointsPerMistake: Int

    var currentScore by mutableStateOf(0)
        private set

    init {
        val gridPointsMap = mapOf(
            Pair(2, 3) to Pair(10, 2),
            Pair(2, 4) to Pair(15, 4),
            Pair(3, 4) to Pair(20, 6),
            Pair(4, 4) to Pair(25, 8)
        )

        // Punkte aus Map holen oder Standardwert verwenden (10 Punkte für Match, 3 Punkte für Fehler)
        val points = gridPointsMap[Pair(rows, columns)] ?: Pair(10, 3)

        pointsPerMatch = points.first
        pointsPerMistake = points.second
    }

    // Punkte erhöhen, wenn Paar gefunden wurde
    fun onMatchFound() {
        currentScore += pointsPerMatch
    }

    // Punkte reduzieren bei einem Fehler (kein passendes Paar)
    fun onMismatch() {
        currentScore -= pointsPerMistake
        if (currentScore < 0) currentScore = 0 // verhindert negative Punkte
    }

    // Punkte zurücksetzen (neues Spiel)
    fun resetScore() {
        currentScore = 0
    }

    fun applyTimeBonus(totalSeconds: Int, rows: Int, columns: Int) {
        val timeBonus = when (Pair(rows, columns)) {
            Pair(2, 3) -> when {
                totalSeconds < 10 -> 10
                totalSeconds < 15 -> 5
                else -> 0
            }
            Pair(2, 4) -> when {
                totalSeconds < 25 -> 15
                totalSeconds < 35 -> 7
                else -> 0
            }
            Pair(3, 4) -> when {
                totalSeconds < 40 -> 20
                totalSeconds < 55 -> 10
                else -> 0
            }
            Pair(4, 4) -> when {
                totalSeconds < 60 -> 30
                totalSeconds < 80 -> 10
                else -> 0
            }
            else -> 0
        }

        currentScore += timeBonus
    }

}
