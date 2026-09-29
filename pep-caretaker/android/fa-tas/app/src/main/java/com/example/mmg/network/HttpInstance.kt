package com.example.mmg.network

import android.util.Log
import com.example.mmg.BuildConfig
import com.example.mmg.dto.MmgDto
import com.example.mmg.dto.StepDto
import com.example.mmg.network.service.MmgApiService
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpInstance {
    companion object {
        private const val BACKEND_URL = "https://vm107.htl-leonding.ac.at/"

        // Hier stand ein X509TrustManager, der jedes Zertifikat annahm, plus
        // hostnameVerifier { _, _ -> true }. Damit war das HTTPS wirkungslos und
        // das Client-Secret aus BuildConfig samt Access-Token aufbrechbar.
        // vm107 hat ein regulaeres Let's-Encrypt-Zertifikat.
        private val baseClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

        // Token-Anfragen laufen über denselben Client (gleiche TLS-Einstellungen), aber ohne Interceptor
        private val client = baseClient.newBuilder()
            .addInterceptor(
                RobotAuthInterceptor(
                    tokenUrl = RobotAuthInterceptor.tokenUrl(BuildConfig.KEYCLOAK_URL),
                    clientId = BuildConfig.ROBOT_CLIENT_ID,
                    clientSecret = BuildConfig.ROBOT_CLIENT_SECRET,
                    tokenClient = baseClient,
                )
            )
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        private val apiService: MmgApiService = retrofit.create(MmgApiService::class.java)

        suspend fun fetchMmgDtos(): List<MmgDto>? =
            withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getMmgDtos()
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        Log.e("API", "Error: ${response.code()} - ${response.message()}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("API", "Exception: ${e.message}", e)
                    null
                }
            }

        suspend fun fetchMmgSteps(id: Int): List<StepDto>? =
            withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getSteps(id)
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        Log.e("API", "Error: ${response.code()} - ${response.message()}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("API", "Exception: ${e.message}", e)
                    null
                }
            }

        suspend fun fetchImage(id: Int): ByteArray? =
            withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getImage(id)
                    if (response.isSuccessful) {
                        response.body()?.bytes()
                    } else {
                        Log.e("API", "Error fetching image: ${response.code()} - ${response.message()}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("API", "Exception fetching image: ${e.message}", e)
                    null
                }
            }
    }
}