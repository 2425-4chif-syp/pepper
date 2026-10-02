package com.example.mmg.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    val DarkTeal = Color(0xFF348888)
    val BrightTeal = Color(0xFF22BABB)
    val MintGreen = Color(0xFF9EF8EE)
    val Orange = Color(0xFFFA7F08)
    val RedOrange = Color(0xFFF24405)

    // Standard Material Colors
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
}

/**
 * Farben der Weboberflaeche (pep-caretaker/frontend, DaisyUI-Themes "pepper" und
 * "pepper-dark" in tailwind.config.js). Hier 1:1 uebernommen, damit Tablet und Web
 * dieselbe Sprache sprechen: neutrale Flaechen, genau ein Akzent (Pepper-Teal).
 *
 * Namensschema folgt dem Web: base100 traegt Karten, base200 die Seitenflaeche,
 * base300 die Linien. Im Dunkelmodus liegt base100 UEBER base200 - die Karte ist
 * heller als der Hintergrund, sonst wirkt sie wie ein Loch statt wie eine
 * angehobene Flaeche.
 */
object PepperColors {
    // Helles Theme ("pepper")
    val Primary = Color(0xFF2F8080)
    val PrimaryContent = Color(0xFFFFFFFF)
    val Base100 = Color(0xFFFFFFFF)
    val Base200 = Color(0xFFF5F5F7)
    val Base300 = Color(0xFFD2D2D7)
    val BaseContent = Color(0xFF1D1D1F)
    // Zweitrangige Beschriftungen; im Web die abgeschwaechte base-content-Stufe
    val BaseContentMuted = Color(0xFF6E6E73)
    // Fuellung fuer sekundaere Schaltflaechen: einen Hauch dunkler als die
    // Seitenflaeche, damit sie auch auf einer weissen Karte sichtbar bleibt
    val Fill = Color(0xFFE8E8ED)
    val Error = Color(0xFFFF3B30)
    val ErrorContent = Color(0xFFFFFFFF)

    // Dunkles Theme ("pepper-dark")
    val PrimaryDark = Color(0xFF4DB6B6)
    val PrimaryContentDark = Color(0xFF00201F)
    val Base100Dark = Color(0xFF1C1C1E)
    val Base200Dark = Color(0xFF000000)
    val Base300Dark = Color(0xFF3A3A3C)
    val BaseContentDark = Color(0xFFF5F5F7)
    val BaseContentMutedDark = Color(0xFF98989D)
    val FillDark = Color(0xFF2C2C2E)
    val ErrorDark = Color(0xFFFF453A)
    val ErrorContentDark = Color(0xFFFFFFFF)
}
