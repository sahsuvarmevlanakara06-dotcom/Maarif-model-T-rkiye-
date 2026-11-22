package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class MaarifExtendedColors(
    val headerNavy: Color,
    val headerCrimsonBorder: Color,
    val streakColor: Color,
    val streakBg: Color,
    val goldColor: Color,
    val goldDark: Color,
    val goldBg: Color,
    val emerald: Color,
    val emeraldBg: Color,
    val potionViolet: Color,
    val potionVioletBg: Color,
    val cardBorder: Color,
    val subtleSurface: Color
)

val LightExtendedColors = MaarifExtendedColors(
    headerNavy = Slate900,
    headerCrimsonBorder = MaarifCrimson,
    streakColor = StreakFlame,
    streakBg = StreakFlameSoft,
    goldColor = GoldAmber,
    goldDark = GoldAmberDark,
    goldBg = GoldAmberSoft,
    emerald = EmeraldSuccess,
    emeraldBg = EmeraldSoft,
    potionViolet = PotionViolet,
    potionVioletBg = PotionVioletSoft,
    cardBorder = Slate200,
    subtleSurface = Slate100
)

val DarkExtendedColors = MaarifExtendedColors(
    headerNavy = Color(0xFF090F1E),
    headerCrimsonBorder = MaarifCrimson,
    streakColor = Color(0xFFFB923C),
    streakBg = Color(0xFF431407),
    goldColor = Color(0xFFFBBF24),
    goldDark = Color(0xFFFDE68A),
    goldBg = Color(0xFF451A03),
    emerald = Color(0xFF34D399),
    emeraldBg = Color(0xFF064E3B),
    potionViolet = Color(0xFFA78BFA),
    potionVioletBg = Color(0xFF2E1065),
    cardBorder = Slate700,
    subtleSurface = Slate800
)

val LocalMaarifColors = staticCompositionLocalOf { LightExtendedColors }

private val LightColorScheme = lightColorScheme(
    primary = MaarifCrimson,
    onPrimary = Color.White,
    primaryContainer = MaarifCrimsonSoft,
    onPrimaryContainer = MaarifCrimsonDark,
    secondary = Slate900,
    onSecondary = Color.White,
    secondaryContainer = Slate200,
    onSecondaryContainer = Slate900,
    tertiary = RoyalBlue,
    onTertiary = Color.White,
    tertiaryContainer = RoyalBlueSoft,
    onTertiaryContainer = Slate900,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200,
    error = RoseError,
    onError = Color.White,
    errorContainer = RoseErrorSoft,
    onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEF4444),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5C0A18),
    onPrimaryContainer = Color(0xFFFFD9DF),
    secondary = Slate200,
    onSecondary = Slate900,
    secondaryContainer = Slate800,
    onSecondaryContainer = Slate100,
    tertiary = Color(0xFF60A5FA),
    onTertiary = Slate900,
    tertiaryContainer = Color(0xFF1E3A8A),
    onTertiaryContainer = Color(0xFFDBEAFE),
    background = Color(0xFF0B1120),
    onBackground = Slate50,
    surface = Color(0xFF111827),
    onSurface = Slate50,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    outline = Slate600,
    outlineVariant = Slate700,
    error = Color(0xFFF87171),
    onError = Slate900,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

val MaarifShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MaarifPortalTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalMaarifColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MaarifTypography,
            shapes = MaarifShapes,
            content = content
        )
    }
}
