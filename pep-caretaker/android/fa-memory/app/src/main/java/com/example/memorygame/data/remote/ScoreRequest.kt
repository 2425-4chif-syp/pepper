package com.example.memorygame.data.remote

import com.example.memorygame.data.model.Game
import com.example.memorygame.data.model.Person

data class ScoreRequest(
    val comment: String,
    val dateTime: String,
    val elapsedTime: Int,
    val score: Int,
    val person: Person, // ❗ statt PersonRef
    val game: Game      // ❗ statt GameRef
)

data class PersonRef(val id: Long)
data class GameRef(val id: Long)
