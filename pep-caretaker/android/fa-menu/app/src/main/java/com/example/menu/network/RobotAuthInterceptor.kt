package com.example.menu.network

import android.util.Log
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

/**
 * Meldet die App beim Backend als Keycloak-Service-Account "pepper-robot" an.
 *
 * Holt per Client-Credentials einen Access-Token, cached ihn bis kurz vor Ablauf und hängt ihn
 * als "Authorization: Bearer ..." an jede Anfrage. Antwortet das Backend trotzdem mit 401
 * (z. B. Token widerrufen), wird der Token einmal neu geholt und die Anfrage wiederholt.
 * Client-ID/Secret und Keycloak-URL kommen aus BuildConfig (siehe app/build.gradle).
 */
class RobotAuthInterceptor(
    private val tokenUrl: String,
    private val clientId: String,
    private val clientSecret: String,
    private val tokenClient: OkHttpClient = OkHttpClient(),
) : Interceptor {

    private var token: String? = null
    private var expiresAtMillis = 0L

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(authorized(chain.request(), forceRefresh = false))
        if (response.code != 401) return response
        response.close()
        return chain.proceed(authorized(chain.request(), forceRefresh = true))
    }

    private fun authorized(request: Request, forceRefresh: Boolean): Request {
        val accessToken = currentToken(forceRefresh) ?: return request
        return request.newBuilder().header("Authorization", "Bearer $accessToken").build()
    }

    @Synchronized
    private fun currentToken(forceRefresh: Boolean): String? {
        if (!forceRefresh && token != null && System.currentTimeMillis() < expiresAtMillis) return token
        if (clientSecret.isEmpty()) {
            Log.e(TAG, "PEPPER_ROBOT_CLIENT_SECRET fehlt – Anfragen gehen ohne Token raus")
            return null
        }

        val body = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()
        tokenClient.newCall(Request.Builder().url(tokenUrl).post(body).build()).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e(TAG, "Token-Anfrage fehlgeschlagen: ${response.code}")
                return null
            }
            val json = JSONObject(response.body?.string().orEmpty())
            token = json.getString("access_token")
            // 30 s vor Ablauf erneuern, damit kein Request mit abgelaufenem Token rausgeht
            val validSeconds = (json.optLong("expires_in", 60) - 30).coerceAtLeast(5)
            expiresAtMillis = System.currentTimeMillis() + validSeconds * 1000
            return token
        }
    }

    companion object {
        private const val TAG = "RobotAuth"

        fun tokenUrl(keycloakUrl: String) = keycloakUrl.trimEnd('/') + "/realms/pepper/protocol/openid-connect/token"
    }
}
