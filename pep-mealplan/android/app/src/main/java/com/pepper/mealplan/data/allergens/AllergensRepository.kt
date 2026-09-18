package com.pepper.mealplan.data.allergens

import com.pepper.mealplan.network.RetrofitClient
import com.pepper.mealplan.network.dto.AllergenDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AllergensRepository {
    private val api = RetrofitClient.allergensApi

    suspend fun getAllergens(): Result<List<AllergenDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllergens()
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
