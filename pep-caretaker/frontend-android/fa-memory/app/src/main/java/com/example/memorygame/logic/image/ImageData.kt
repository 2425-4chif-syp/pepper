package com.example.memorygame.logic.image

import androidx.annotation.DrawableRes

sealed class ImageData {
    data class Base64Image(val base64: String): ImageData()
    data class DrawableImage(@DrawableRes val resId: Int): ImageData()
}
