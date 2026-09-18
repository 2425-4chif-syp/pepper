package com.example.menu.dto

import com.google.gson.annotations.SerializedName

data class Person(
    @SerializedName("id") val pid: Long,
    val firstName: String,
    val lastName: String,
    val gender: Boolean,       // Achtung: Bedeutung siehe Punkt 3
    val isWorker: Boolean,
    val dob: String? = null,
    val roomNo: String? = null
)

/*
data class Person(
    val pid: Number,
    val firstName: String,
    val lastName: String,
    // True = Mann
    // False = Frau
    val gender: Boolean,
    val isWorker: Boolean
)*/