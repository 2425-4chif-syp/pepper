package com.pepper.mealplan.network.api

import com.pepper.mealplan.network.dto.ResidentCreateDto
import com.pepper.mealplan.network.dto.ResidentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ResidentsApiService {
    
    @GET("mealplan/api/residents")
    suspend fun getResidents(): Response<List<ResidentDto>>
    
    @POST("mealplan/api/residents")
    suspend fun addResident(@Body resident: ResidentCreateDto): Response<ResidentDto>
    
    @DELETE("mealplan/api/residents/{id}")
    suspend fun deleteResident(@Path("id") id: Int): Response<Unit>
}
