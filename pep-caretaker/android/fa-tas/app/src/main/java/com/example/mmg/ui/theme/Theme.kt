package com.example.mmg.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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