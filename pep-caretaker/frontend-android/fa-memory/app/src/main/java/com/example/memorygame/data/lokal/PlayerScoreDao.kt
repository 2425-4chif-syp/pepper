package com.example.memorygame.data.lokal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerScoreDao {

    @Query("SELECT * FROM player_scores")
    fun getAllScores(): Flow<List<LocalPlayerScore>>

    @Insert
    suspend fun insertScore(score: LocalPlayerScore)

    @Query("SELECT * FROM player_scores WHERE personId = :personId ORDER BY score DESC")
    fun getScoresByPerson(personId: Long): Flow<List<LocalPlayerScore>>

}
