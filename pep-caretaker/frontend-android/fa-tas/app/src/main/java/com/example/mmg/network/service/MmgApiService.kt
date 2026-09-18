package com.example.mmg.network.service

import com.example.mmg.dto.MmgDto
import com.example.mmg.dto.StepDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MmgApiService {
    @GET("api/tagalongstories")
    suspend fun getMmgDtos(): Response<List<MmgDto>>

    @GET("api/tagalongstories/{storyId}/steps")
    suspend fun getSteps(@Path("storyId") storyId: Int): Response<List<StepDto>>

    @GET("imagor/unsafe/fit-in/800x0/http%3A%2F%2Fbackend%3A8080%2Fapi%2Fimage%2Fpicture%2F{id}?ngsw-bypass=true")
    suspend fun getImage(@Path("id") id: Int): Response<ResponseBody>
}

///imagor/unsafe/fit-in/800x0/http%3A%2F%2Fbackend%3A8080%2Fapi%2Fimage%2Fpicture%2F{id}?ngsw-bypass=true"
//api/image/picture/{id}