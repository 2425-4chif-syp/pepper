package com.pepper.mealplan.network.api

import com.pepper.mealplan.network.dto.ApiMealPlanDto
import com.pepper.mealplan.network.dto.ApiMealPlanUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MenuApiService {
    
    @GET("mealplan/api/menu/week/{weekNumber}")
    suspend fun getWeekPlan(@Path("weekNumber") weekNumber: Int): Response<List<ApiMealPlanDto>>
    
    @GET("mealplan/api/menu/day/{weekNumber}/{weekDay}")
    suspend fun getMenuForDate(
        @Path("weekNumber") weekNumber: Int,
        @Path("weekDay") weekDay: Int
    ): Response<ApiMealPlanDto>
    
    @POST("mealplan/api/menu/week")
    suspend fun updateWeekMenu(@Body plans: List<ApiMealPlanUpdateDto>): Response<Any>
}
