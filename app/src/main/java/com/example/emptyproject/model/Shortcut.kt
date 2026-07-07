package com.example.emptyproject.model

import androidx.compose.ui.graphics.Color

data class Shortcut(
    val id: String,
    val title: String,
    val url: String,
    val accentColor: Color,
    val initial: String,
)

object DefaultShortcuts {
    val items = listOf(
        Shortcut(
            id = "wikipedia",
            title = "Wikipedia",
            url = "https://en.wikipedia.org",
            accentColor = Color(0xFF000000),
            initial = "W",
        ),
        Shortcut(
            id = "news",
            title = "Daily News",
            url = "https://news.google.com",
            accentColor = Color(0xFF4285F4),
            initial = "N",
        ),
        Shortcut(
            id = "kotlin",
            title = "Kotlin Docs",
            url = "https://kotlinlang.org/docs/home.html",
            accentColor = Color(0xFF7F52FF),
            initial = "K",
        ),
    )
}
