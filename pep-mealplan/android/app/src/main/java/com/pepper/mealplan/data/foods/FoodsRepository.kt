package com.pepper.mealplan.data.foods

import com.pepper.mealplan.network.RetrofitClient
import com.pepper.mealplan.network.dto.ApiFoodDto
import com.pepper.mealplan.network.dto.FoodCreateDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FoodsRepository {
    private val api = RetrofitClient.foodsApi

    suspend fun getAllFoods(): Result<List<ApiFoodDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllFoods()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFoodById(id: Int): Result<ApiFoodDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFoodById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFoodsByName(name: String): Result<List<ApiFoodDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFoodsByName(name)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFoodsByType(type: String): Result<List<ApiFoodDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFoodsByType(type)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFood(name: String, type: String, picture: String? = null): Result<ApiFoodDto> = withContext(Dispatchers.IO) {
        try {
            val food = FoodCreateDto(name = name, type = type, picture = picture)
            val response = api.addFood(food)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFood(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteFood(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
