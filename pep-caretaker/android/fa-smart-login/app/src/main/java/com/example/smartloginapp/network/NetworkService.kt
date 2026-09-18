package com.example.smartloginapp.network

import retrofit2.Response
import retrofit2.http.GET

// Antwortdatenklasse
data class UserResponse(val name: String)

// Retrofit-Service Interface
interface NetworkService {
    @GET("path/to/your/endpoint")  // API-Endpunkt anpassen
    suspend fun getUserName(): Response<UserResponse>
}