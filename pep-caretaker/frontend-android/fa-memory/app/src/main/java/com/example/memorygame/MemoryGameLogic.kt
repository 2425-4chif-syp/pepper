package com.example.memorygame

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.memorygame.logic.ScoreManager
import com.example.memorygame.logic.image.ImageData
import kotlinx.coroutines.delay

data class MemoryCard(val id: Int, val image: ImageData, var isFlipped: Boolean = false, var isMatched: Boolean = false)

val cardImages = listOf(
    R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
    R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8
)

class GameLogic(private val scoreManager: ScoreManager) {
    var flippedCards by mutableStateOf(mutableListOf<Int>())
    var matchedCards by mutableStateOf(mutableSetOf<Int>())
    var isGameOver by mutableStateOf(false)

    suspend fun flipCard(cardIndex: Int, allCards: MutableList<MemoryCard>) {
        if (cardIndex !in flippedCards && flippedCards.size < 2 && cardIndex !in matchedCards) {
            flippedCards = mutableListOf(*flippedCards.toTypedArray(), cardIndex)
            allCards[cardIndex].isFlipped = true

            if (flippedCards.size == 2) {
                delay(300)
                checkForMatch(allCards)
            }
        }
    }

    private fun checkForMatch(allCards: MutableList<MemoryCard>) {
        if (flippedCards.size == 2) {
            val firstCardIndex = flippedCards[0]
            val secondCardIndex = flippedCards[1]
            val firstCard = allCards[firstCardIndex]
            val secondCard = allCards[secondCardIndex]

            if (firstCard.image == secondCard.image) {
                matchedCards.add(firstCardIndex)
                matchedCards.add(secondCardIndex)
                scoreManager.onMatchFound()
            } else {
                firstCard.isFlipped = false
                secondCard.isFlipped = false
                scoreManager.onMismatch()
            }

            flippedCards = mutableListOf()

            if (matchedCards.size == allCards.size) {
                isGameOver = true
            }
        }
    }
}