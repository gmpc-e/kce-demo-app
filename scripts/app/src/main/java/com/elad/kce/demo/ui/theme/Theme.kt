package com.elad.kce.demo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

private val DarkScheme: ColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = PurpleOnPrimary,
    primaryContainer = PurpleContainer,
    onPrimaryContainer = PurpleOnContainer,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceDark,
    outline = OutlineDark,
)

private val AppTypography = Typography(
    // defaults are fine; Hebrew looks good with system font
    bodyLarge = Typography().bodyLarge.copy(fontFamily = FontFamily.Default),
    titleLarge = Typography().titleLarge.copy(fontFamily = FontFamily.Default),
    titleMedium = Typography().titleMedium.copy(fontFamily = FontFamily.Default),
)

@Composable
fun KceTheme(content: @Composable () -> Unit) {
    val scheme = DarkScheme
    MaterialTheme(colorScheme = scheme, typography = AppTypography, content = content)
}