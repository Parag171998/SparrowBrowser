package com.example.emptyproject.util

import java.net.URLEncoder

fun resolveInputToUrl(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return trimmed
    if (trimmed.contains("://")) return trimmed
    if (trimmed.matches(Regex("""^[\w-]+(\.[\w-]+)+.*"""))) return "https://$trimmed"
    return "https://www.google.com/search?q=${URLEncoder.encode(trimmed, Charsets.UTF_8.name())}"
}
