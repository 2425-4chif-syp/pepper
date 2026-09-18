package com.example.memorygame.data.model

data class PlayerScore(
    val comment: String,
    val dateTime: String,
    val elapsedTime: Int,
    val score: Int,
    val person: Person,
    val game: Game
)

data class Person(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val dob: String,
    val gender: Boolean,
    val isWorker: Boolean,
    val roomNo: String
)

data class Game(
    val id: Long,
    val name: String,
    val enabled: Boolean,
    val gameType: GameType
)

data class GameType(
    val id: String,
    val name: String
)

