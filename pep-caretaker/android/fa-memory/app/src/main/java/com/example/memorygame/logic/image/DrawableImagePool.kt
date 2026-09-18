package com.example.memorygame.logic.image

import com.example.memorygame.R

object DrawableImagePool {

    private val availableDrawables = listOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4,
        R.drawable.image5,
        R.drawable.image6,
        R.drawable.image7,
        R.drawable.image8
    )

    fun getRandomImages(count: Int): List<Int> {
        require(count <= availableDrawables.size) {
            "Nicht genug drawable-Bilder verfügbar: benötigt $count, vorhanden ${availableDrawables.size}"
        }
        return availableDrawables.shuffled().take(count)
    }
}
