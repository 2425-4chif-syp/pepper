package com.pepper.mealplan.data.residents

import com.pepper.mealplan.network.RetrofitClient
import com.pepper.mealplan.network.dto.ResidentCreateDto
import com.pepper.mealplan.network.dto.ResidentDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResidentsRepository {
    private val api = RetrofitClient.residentsApi

    suspend fun getResidents(): Result<List<ResidentDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getResidents()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addResident(
        firstname: String,
        lastname: String,
        dob: String? = null,
        faceId: String? = null
    ): Result<ResidentDto> = withContext(Dispatchers.IO) {
        try {
            val resident = ResidentCreateDto(
                firstname = firstname,
                lastname = lastname,
                dob = dob,
                faceId = faceId
            )
            val response = api.addResident(resident)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteResident(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteResident(id)
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
