package com.pepper.mealplan.network.dto

data class PictureFileDto(
    val id: Int? = null,
    val bytes: String,
    val name: String,
    val mediaType: String
)