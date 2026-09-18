package com.example.memorygame.data.remote

import com.example.memorygame.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "http://vm107.htl-leonding.ac.at:8080/"

    // private const val BASE_URL = "http://10.0.2.2:8080/" // für Android Emulator (Kein-Pepper-Deployment)

    //private const val BASE_URL= "http://172.18.200.84:8080/" //ipconfig(im cmd) danach IP4v Adress hier eintragen um mit Pepper kommunizieren können

    private val client = OkHttpClient.Builder()
        .addInterceptor(
            RobotAuthInterceptor(
                tokenUrl = RobotAuthInterceptor.tokenUrl(BuildConfig.KEYCLOAK_URL),
                clientId = BuildConfig.ROBOT_CLIENT_ID,
                clientSecret = BuildConfig.ROBOT_CLIENT_SECRET,
            )
        )
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun providePersonApi(): PersonApi {
        return retrofit.create(PersonApi::class.java)
    }
}
