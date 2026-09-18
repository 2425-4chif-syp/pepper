package com.pepper.mealplan.network.dto

data class FoodDto(
    val id: Int? = null,
    val name: String,
    val pictureId: Int? = null,
    val type: String
)