package com.pepper.mealplan.network.dto

data class FoodCreateDto(
    val name: String,
    val type: String,
    val picture: String? = null
)
