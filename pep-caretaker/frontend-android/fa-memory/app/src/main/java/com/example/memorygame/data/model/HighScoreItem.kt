package com.example.memorygame.data.model

data class HighScoreItem(
    val personId: Long,
    val firstName: String,
    val lastName: String,
    val gridRows: Int,
    val gridColumns: Int,
    val score: Int,
    val elapsedTime: Int,
    val date: String
)
