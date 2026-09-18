package com.pepper.mealplan.network.api

import com.pepper.mealplan.network.dto.AllergenDto
import retrofit2.Response
import retrofit2.http.GET

interface AllergensApiService {
    
    @GET("mealplan/api/allergens")
    suspend fun getAllergens(): Response<List<AllergenDto>>
}
