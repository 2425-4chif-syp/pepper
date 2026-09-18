package com.example.memorygame.logic.image

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

import android.util.Log

object ImageDecoder {

    fun decodeBase64ToImageBitmap(base64: String): ImageBitmap? {
        return try {
            val cleanBase64 = if (base64.contains(",")) {
                base64.substringAfter(",")
            } else {
                base64
            }

            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            Log.e("ImageDecoder", "Fehler beim Decodieren von Base64: ${e.message}", e)
            null
        }
    }
}
