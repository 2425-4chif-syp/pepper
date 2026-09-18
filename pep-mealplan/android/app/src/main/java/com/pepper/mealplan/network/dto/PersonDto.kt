package com.pepper.mealplan.network.dto

import java.time.LocalDate

data class PersonDto(
    val id: Int? = null,
    val firstname: String,
    val lastname: String,
    val dob: LocalDate? = null
)