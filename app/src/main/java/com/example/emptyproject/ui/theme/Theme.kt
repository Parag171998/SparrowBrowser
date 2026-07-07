package com.example.emptyproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SparrowColorScheme = lightColorScheme(
    primary = BrowserBlue,
    onPrimary = Color(0xFFFFFFFF),
    secondary = BrowserGray,
    background = BrowserBackground,
    surface = BrowserSurface,
    onBackground = Color(0xFF202124),
    onSurface = Color(0xFF202124),
)

@Composable
fun SparrowBrowserTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SparrowColorScheme,
        typography = Typography,
        content = content,
    )
}
