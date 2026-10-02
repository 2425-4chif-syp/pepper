package com.example.mmg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Benutzerdefinierte Light Theme
private val TealLightColorScheme = lightColorScheme(
    primary = AppColors.DarkTeal,
    onPrimary = AppColors.White,
    secondary = AppColors.BrightTeal,
    onSecondary = AppColors.White,
    tertiary = AppColors.MintGreen,
    onTertiary = AppColors.Black,
    background = AppColors.White,
    onBackground = AppColors.Black,
    surface = AppColors.MintGreen,
    onSurface = AppColors.Black
)

// Orange Theme
private val OrangeLightColorScheme = lightColorScheme(
    primary = AppColors.Orange,
    onPrimary = AppColors.White,
    secondary = AppColors.RedOrange,
    onSecondary = AppColors.White,
    tertiary = AppColors.MintGreen,
    onTertiary = AppColors.Black,
    background = AppColors.White,
    onBackground = AppColors.Black,
    surface = AppColors.MintGreen,
    onSurface = AppColors.Black
)

// Teal Theme
private val BrightTealColorScheme = lightColorScheme(
    primary = AppColors.BrightTeal,
    onPrimary = AppColors.White,
    secondary = AppColors.DarkTeal,
    onSecondary = AppColors.White,
    tertiary = AppColors.Orange,
    onTertiary = AppColors.White,
    background = AppColors.White,
    onBackground = AppColors.Black,
    surface = AppColors.MintGreen,
    onSurface = AppColors.Black
)

/**
 * Dieselbe Rollenverteilung wie im Web-Theme "pepper":
 * background = Seitenflaeche (base-200), surface = Karte (base-100),
 * primary = der eine Akzent, outline = Linien (base-300).
 *
 * surfaceTint bleibt transparent: Material3 wuerde Flaechen sonst mit der
 * Akzentfarbe einfaerben, je hoeher sie liegen. Die Karten sollen aber genau so
 * weiss sein wie im Web.
 */
private val PepperLightColorScheme = lightColorScheme(
    primary = PepperColors.Primary,
    onPrimary = PepperColors.PrimaryContent,
    primaryContainer = PepperColors.Primary,
    onPrimaryContainer = PepperColors.PrimaryContent,
    secondary = PepperColors.Fill,
    onSecondary = PepperColors.BaseContent,
    secondaryContainer = PepperColors.Fill,
    onSecondaryContainer = PepperColors.BaseContent,
    tertiary = PepperColors.Primary,
    onTertiary = PepperColors.PrimaryContent,
    background = PepperColors.Base200,
    onBackground = PepperColors.BaseContent,
    surface = PepperColors.Base100,
    onSurface = PepperColors.BaseContent,
    surfaceVariant = PepperColors.Base200,
    onSurfaceVariant = PepperColors.BaseContentMuted,
    surfaceTint = Color.Transparent,
    outline = PepperColors.Base300,
    outlineVariant = PepperColors.Base300,
    error = PepperColors.Error,
    onError = PepperColors.ErrorContent
)

private val PepperDarkColorScheme = darkColorScheme(
    primary = PepperColors.PrimaryDark,
    onPrimary = PepperColors.PrimaryContentDark,
    primaryContainer = PepperColors.PrimaryDark,
    onPrimaryContainer = PepperColors.PrimaryContentDark,
    secondary = PepperColors.FillDark,
    onSecondary = PepperColors.BaseContentDark,
    secondaryContainer = PepperColors.FillDark,
    onSecondaryContainer = PepperColors.BaseContentDark,
    tertiary = PepperColors.PrimaryDark,
    onTertiary = PepperColors.PrimaryContentDark,
    background = PepperColors.Base200Dark,
    onBackground = PepperColors.BaseContentDark,
    surface = PepperColors.Base100Dark,
    onSurface = PepperColors.BaseContentDark,
    surfaceVariant = PepperColors.FillDark,
    onSurfaceVariant = PepperColors.BaseContentMutedDark,
    surfaceTint = Color.Transparent,
    outline = PepperColors.Base300Dark,
    outlineVariant = PepperColors.Base300Dark,
    error = PepperColors.ErrorDark,
    onError = PepperColors.ErrorContentDark
)

/**
 * Theme der App. Farben, Typo und Radien kommen aus dem Web-Design-System.
 *
 * Das Pepper-Tablet laeuft auf Android 6 und kennt keine Systemeinstellung fuer
 * Dunkelmodus - dort greift immer das helle Theme. Auf neueren Geraeten (Emulator,
 * Testtablet) folgt es der Systemeinstellung.
 */
@Composable
fun PepperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) PepperDarkColorScheme else PepperLightColorScheme,
        typography = PepperTypography,
        shapes = PepperMaterialShapes,
        content = content
    )
}

@Composable
fun TealTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TealLightColorScheme,
        content = content
    )
}

@Composable
fun OrangeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OrangeLightColorScheme,
        content = content
    )
}

@Composable
fun BrightTealTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BrightTealColorScheme,
        content = content
    )
}
