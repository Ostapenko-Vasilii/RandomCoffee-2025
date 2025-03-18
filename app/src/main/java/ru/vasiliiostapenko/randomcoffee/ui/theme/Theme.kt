package ru.vasiliiostapenko.randomcoffee.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

private val DarkColorScheme = darkColorScheme(
    primary = MainCardColorDark,
    secondary = AccentColorDark,
    tertiary = MainTextColorDark,
    background = MainBgDark,
    surfaceTint = HighlightColorDark

)

private val LightColorScheme = lightColorScheme(
    primary = MainCardColorLight,
    secondary = AccentColorLight,
    tertiary = MainTextColorLight,
    background = MainBgLight,
    surfaceTint = HighlightColorLight,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
private val LightTypography = Typography(
    bodyLarge = TextStyle(
        color = Color.Black,
    ),
)

private val DarkTypography = Typography(
    bodyLarge = TextStyle(
        color = Color.White,
    ),
)

@Composable
fun RandomCoffeeTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = if (darkTheme) DarkTypography else LightTypography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
