package com.example.memorygame.data.remote

import com.example.memorygame.data.model.PlayerScore
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScoreApi {
        @GET("api/gamescore/player/{playerId}")
        suspend fun getScoresForPlayer(@Path("playerId") playerId: Long): List<PlayerScore>

        @POST("api/gamescore")
        suspend fun submitScore(@Body scoreRequest: ScoreRequest): Response<PlayerScore>
}