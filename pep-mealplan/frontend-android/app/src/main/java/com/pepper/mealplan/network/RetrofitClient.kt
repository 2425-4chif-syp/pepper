package com.pepper.mealplan.network

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.pepper.mealplan.network.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import com.pepper.mealplan.network.api.FoodsApiService

object RetrofitClient {
    private const val BASE_URL = "https://vm107.htl-leonding.ac.at/"

    // Create a trust manager that accepts all certificates (for development only!)
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    private val client = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .apply {
            // Configure SSL to trust all certificates (ONLY for development!)
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            hostnameVerifier { _, _ -> true }
        }
        .build()


    suspend fun sendPostRequestImage(imageBitMap: ImageBitmap): String =
        withContext(Dispatchers.IO) {
            val url = BASE_URL + "api/auth/face/verify"

            val bitmap: Bitmap = imageBitMap.asAndroidBitmap()

            // Skaliere das Bild auf eine maximale Größe runter
            val maxWidth = 800
            val maxHeight = 600
            val scaledBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                val ratio = minOf(
                    maxWidth.toFloat() / bitmap.width,
                    maxHeight.toFloat() / bitmap.height
                )
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }

            val byteArrayOutputStream = ByteArrayOutputStream()
            // Verwende JPEG mit 70% Qualität statt PNG mit 100%
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            println("Image size: ${byteArray.size} bytes (${byteArray.size / 1024} KB)")

            val requestBody = RequestBody.create(
                "application/octet-stream".toMediaTypeOrNull(),
                byteArray
            )

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                println("Status Code: ${response.code}")
                println("Response Body: $responseBody")

                if (responseBody.isNullOrEmpty()) {
                    println("Error: Response is empty or null!")
                    ""
                } else {
                    responseBody
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
                e.printStackTrace()
                ""
            }
        }
    suspend fun fetchImage(id: Int): ByteArray? =
        withContext(Dispatchers.IO) {
            try {
                val response = foodsApi.getImage(id)
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

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val ordersApi: OrdersApiService by lazy {
        retrofit.create(OrdersApiService::class.java)
    }

    val menuApi: MenuApiService by lazy {
        retrofit.create(MenuApiService::class.java)
    }

    val foodsApi: FoodsApiService by lazy {
        retrofit.create(FoodsApiService::class.java)
    }

    val allergensApi: AllergensApiService by lazy {
        retrofit.create(AllergensApiService::class.java)
    }

    val residentsApi: ResidentsApiService by lazy {
        retrofit.create(ResidentsApiService::class.java)
    }

}
