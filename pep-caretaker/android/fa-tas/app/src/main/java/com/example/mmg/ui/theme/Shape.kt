package com.example.mmg.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Radien aus src/styles.css (--r-control, --r-card, --r-sheet, --r-pill).
 * Groessere Flaechen bekommen groessere Radien - so bleibt die Kruemmung
 * im Verhaeltnis zur Flaeche gleich.
 */
object PepperShapes {
    /** --r-control: Eingaben, kleine Flaechen */
    val control = RoundedCornerShape(10.dp)
    /** --r-card: Karten der Uebersichtsliste */
    val card = RoundedCornerShape(14.dp)
    /** --r-sheet: grosse Flaechen, Bildbuehne */
    val sheet = RoundedCornerShape(18.dp)
    /** --r-pill / --rounded-btn: 980px im Web, hier voll gerundet */
    val pill = RoundedCornerShape(percent = 50)
}

val PepperMaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = PepperShapes.control,
    medium = PepperShapes.card,
    large = PepperShapes.sheet,
    extraLarge = RoundedCornerShape(24.dp)
)
