package com.example.appbookshelf.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA4D1BF),
    secondary = Color(0xFFBCCCBD),
    tertiary = Color(0xFFE5C18D),
    background = Color(0xFF101512),
    surface = Color(0xFF101512),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF305F4F),
    secondary = Color(0xFF536454),
    tertiary = Color(0xFF7C5930),
    background = Color(0xFFFAFAF4),
    surface = Color(0xFFFAFAF4),
    surfaceContainerLow = Color(0xFFF2F3EC),
    surfaceContainerHighest = Color(0xFFE1E5DC),
    secondaryContainer = Color(0xFFD7E8D7),
)

@Composable
fun APPBookshelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
