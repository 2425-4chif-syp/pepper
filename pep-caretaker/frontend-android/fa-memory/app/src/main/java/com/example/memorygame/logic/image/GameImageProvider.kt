package com.example.memorygame.logic.image

import com.example.memorygame.data.remote.PersonApi
import com.example.memorygame.logic.image.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GameImageProvider {

    suspend fun getImages(
        neededPairs: Int,
        personId: Long?,
        api: PersonApi
    ): List<ImageData> = withContext(Dispatchers.IO) {
        val imageList = mutableListOf<ImageData>()

        //Base64-Bilder vom Backend laden
        val base64Images: List<String> = if (personId != null) {
            try {
                api.getPersonImages(personId).map { it.base64Image }
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        // Base64 in ImageData umwandeln
        val usedBase64 = base64Images.take(neededPairs)
        imageList.addAll(usedBase64.map { ImageData.Base64Image(it) })

        // wenn nicht genug mit mit Drawable auffüllen
        val remaining = neededPairs - imageList.size
        if (remaining > 0) {
            val drawableIds = DrawableImagePool.getRandomImages(remaining)
            imageList.addAll(drawableIds.map { ImageData.DrawableImage(it) })
        }

        // Die Bilder mischen
        return@withContext imageList.shuffled()
    }
}
