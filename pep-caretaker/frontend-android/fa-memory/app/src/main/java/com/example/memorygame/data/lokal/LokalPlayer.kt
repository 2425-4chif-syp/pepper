package com.example.memorygame.data.lokal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_scores")
data class LocalPlayerScore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val personId: Long,
    val firstName: String,
    val lastName: String,
    val grid: String, // z. B. "2x4"
    val score: Int,
    val elapsedTime: Int,
    val date: String // als String für die Anzeige
)
