package com.pepper.mealplan.network.dto

data class OrderUpsertDto(
    val date: String,
    val personId: Int,
    val selectedLunchId: Int,
    val selectedDinnerId: Int
)
