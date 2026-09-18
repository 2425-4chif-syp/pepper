package com.pepper.mealplan.network.api

import com.pepper.mealplan.network.dto.OrderUpsertDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrdersApiService {
    
    @POST("mealplan/api/orders")
    suspend fun upsertOrder(@Body payload: OrderUpsertDto): Response<Any>
    
    @GET("mealplan/api/orders/export/{date}")
    suspend fun getExportedOrders(@Path("date") date: String): Response<List<Any>>
}
