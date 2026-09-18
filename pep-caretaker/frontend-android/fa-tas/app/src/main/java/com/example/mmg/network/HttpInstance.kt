package com.example.mmg.network

import android.util.Log
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
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class HttpInstance {
    companion object {
        private const val BACKEND_URL = "https://vm107.htl-leonding.ac.at/"

        private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        private val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, trustAllCerts, SecureRandom())
        }

        private val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
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