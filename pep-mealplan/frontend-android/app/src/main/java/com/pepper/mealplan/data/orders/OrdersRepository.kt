package com.pepper.mealplan.data.orders

import com.pepper.mealplan.network.RetrofitClient
import com.pepper.mealplan.network.dto.OrderUpsertDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrdersRepository {
    private val api = RetrofitClient.ordersApi

    suspend fun upsertOrder(
        date: String,
        personId: Int,
        selectedLunchId: Int,
        selectedDinnerId: Int
    ): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val payload = OrderUpsertDto(
                date = date,
                personId = personId,
                selectedLunchId = selectedLunchId,
                selectedDinnerId = selectedDinnerId
            )
            val response = api.upsertOrder(payload)
            if (response.isSuccessful) {
                Result.success(response.body() ?: Any())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExportedOrders(date: String): Result<List<Any>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getExportedOrders(date)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
