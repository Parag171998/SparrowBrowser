package com.example.emptyproject.util

object BrowserConstants {
    const val HOME_URL = "https://www.google.com"
    const val MAX_TABS = 10
}

fun String.isHomePage(): Boolean {
    val normalized = trim()
        .removeSuffix("/")
        .lowercase()
    return normalized == BrowserConstants.HOME_URL.lowercase() ||
        normalized == "http://www.google.com"
}
