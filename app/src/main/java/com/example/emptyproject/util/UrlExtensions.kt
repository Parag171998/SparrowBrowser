package com.example.emptyproject.util

import androidx.core.net.toUri

fun extractDomain(url: String): String {
    if (url.isBlank()) return ""
    return try {
        val host = url.toUri().host ?: return url
        host.removePrefix("www.")
    } catch (_: Exception) {
        url
    }
}
