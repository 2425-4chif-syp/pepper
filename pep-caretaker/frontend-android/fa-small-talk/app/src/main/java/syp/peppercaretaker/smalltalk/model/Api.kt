package syp.peppercaretaker.smalltalk.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import syp.peppercaretaker.smalltalk.BuildConfig
import java.util.concurrent.TimeUnit

class Api {
    companion object{
        val BACKEND_URL = BuildConfig.BACKEND_URL

        // Client-Objekt einmal erstellen und wiederverwenden
        private val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

        suspend fun sendPostRequestSmallTalk(said: String): String = withContext(Dispatchers.IO) {
            val url = BACKEND_URL + "api/chat"
            Log.d("Status", "Status Code:")

            val requestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                said
            )

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("Status", "Status Code: ${response.code}")
                Log.d("Response", "Response Body: $responseBody")

                if (responseBody.isNullOrEmpty()) {
                    Log.d("Error", "Response is empty or null!")
                    ""
                } else {
                    responseBody
                }
            } catch (e: Exception) {
                Log.e("Error", "Exception: ${e.message}")
                e.printStackTrace()
                ""
            }
        }
    }
}