package com.pepper.mealplan.network.api

import com.pepper.mealplan.network.dto.ApiFoodDto
import com.pepper.mealplan.network.dto.FoodCreateDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface FoodsApiService {
    
    @GET("mealplan/api/foods")
    suspend fun getAllFoods(): Response<List<ApiFoodDto>>
    
    @GET("mealplan/api/foods/{id}")
    suspend fun getFoodById(@Path("id") id: Int): Response<ApiFoodDto>
    
    @GET("mealplan/api/foods/name/{name}")
    suspend fun getFoodsByName(@Path("name") name: String): Response<List<ApiFoodDto>>
    
    @GET("mealplan/api/foods/type/{type}")
    suspend fun getFoodsByType(@Path("type") type: String): Response<List<ApiFoodDto>>
    
    @POST("mealplan/api/foods")
    suspend fun addFood(@Body food: FoodCreateDto): Response<ApiFoodDto>
    
    @DELETE("mealplan/api/foods/{id}")
    suspend fun deleteFood(@Path("id") id: Int): Response<Unit>

    @GET("mealplan/api/images/picture/{id}")
    suspend fun getImage(@Path("id") id: Int): Response<ResponseBody>
}
