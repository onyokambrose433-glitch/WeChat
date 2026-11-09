package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WeChatOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = WeChatOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = WeChatBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = WeChatBlueDark,
    onSecondaryContainer = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = WeChatOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = WeChatOrangeContainer,
    onPrimaryContainer = WeChatOnOrangeContainer,
    secondary = WeChatBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = WeChatBlueContainer,
    onSecondaryContainer = WeChatOnBlueContainer,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightOutline,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun WeChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
