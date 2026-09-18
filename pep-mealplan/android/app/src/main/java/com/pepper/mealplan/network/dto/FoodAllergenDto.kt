package com.pepper.mealplan.network.dto

data class FoodAllergenDto(
    val id: Int? = null,
    val allergenShortname: String,
    val foodId: Int
)