package com.pepper.mealplan.network.dto

data class ApiMealPlanDto(
    val weekNumber: Int,
    val weekDay: Int,
    val soup: ApiFoodDto? = null,
    val lunch1: ApiFoodDto? = null,
    val lunch2: ApiFoodDto? = null,
    val lunchDessert: ApiFoodDto? = null,
    val dinner1: ApiFoodDto? = null,
    val dinner2: ApiFoodDto? = null
)

data class ApiFoodDto(
    val id: Int? = null,
    val name: String,
    val type: String,
    val picture: PictureFileDto? = null,
    val allergens: List<ApiFoodAllergenDto>? = null
)

data class ApiFoodAllergenDto(
    val id: ApiFoodAllergenIdDto? = null,
    val allergen: AllergenDto? = null
)

data class ApiFoodAllergenIdDto(
    val allergenShortname: String,
    val foodId: Int
)
