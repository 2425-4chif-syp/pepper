package com.pepper.mealplan.network.dto

data class ResidentDto(
    val id: Int? = null,
    val firstname: String,
    val lastname: String,
    val dob: String? = null,
    val faceId: String? = null
)

data class ResidentCreateDto(
    val firstname: String,
    val lastname: String,
    val dob: String? = null,
    val faceId: String? = null
)
