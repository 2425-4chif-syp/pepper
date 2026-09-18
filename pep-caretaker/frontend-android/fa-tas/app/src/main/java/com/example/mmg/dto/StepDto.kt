package com.example.mmg.dto

import com.google.gson.annotations.SerializedName

data class StepDto(
    @SerializedName("index") val index: Int,
    @SerializedName("move") val move: MoveDto? = null,
    @SerializedName("text") val text: String,
    @SerializedName("durationInSeconds") val durationInSeconds: Int,
    @SerializedName("image") val image: ImageDto? = null
)

data class MoveDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)

data class ImageDto(
    @SerializedName("description")val description: String,
    @SerializedName("id")val id: Int,
)