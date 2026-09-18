package com.pepper.mealplan.data.menu

import com.pepper.mealplan.network.RetrofitClient
import com.pepper.mealplan.network.dto.ApiMealPlanDto
import com.pepper.mealplan.network.dto.ApiMealPlanUpdateDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MenuRepository {
    private val api = RetrofitClient.menuApi

    suspend fun getWeekPlan(weekNumber: Int): Result<List<ApiMealPlanDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getWeekPlan(weekNumber)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMenuForDate(weekNumber: Int, weekDay: Int): Result<ApiMealPlanDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMenuForDate(weekNumber, weekDay)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWeekMenu(plans: List<ApiMealPlanUpdateDto>): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateWeekMenu(plans)
            if (response.isSuccessful) {
                Result.success(response.body() ?: Any())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
