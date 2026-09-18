package com.example.memorygame.logic

import com.example.memorygame.R

fun selectImagesForGrid(
    userImages: List<Int>, // Benutzerbilder (IDs aus dem Backend)
    localImages: List<Int>, // Lokale Bilder aus dem drawable-Ordner
    requiredPairs: Int // Anzahl der benötigten Paare
): List<Int> {
    val selectedImages = mutableListOf<Int>()

    // Schritt 1: Prüfen, ob Benutzerbilder ausreichen
    if (userImages.size >= requiredPairs) {
        selectedImages.addAll(userImages.shuffled().take(requiredPairs))
    } else {
        // Schritt 2: Alle Benutzerbilder hinzufügen und fehlende mit lokalen Bildern auffüllen
        selectedImages.addAll(userImages)
        val remainingPairs = requiredPairs - userImages.size
        selectedImages.addAll(localImages.shuffled().take(remainingPairs))
    }

    // Rückgabe der finalen Bildliste
    return selectedImages
}
