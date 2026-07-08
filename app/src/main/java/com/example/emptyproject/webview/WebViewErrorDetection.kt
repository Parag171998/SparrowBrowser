package com.example.emptyproject.webview

import android.webkit.WebView

internal fun isChromeErrorUrl(url: String?): Boolean {
    if (url.isNullOrBlank()) return false
    return url.startsWith("chrome-error://") || url.startsWith("about:neterror")
}

internal fun isWebViewErrorPage(view: WebView?, url: String?): Boolean {
    if (isChromeErrorUrl(url)) return true
    val title = view?.title.orEmpty()
    return title.contains("not available", ignoreCase = true) ||
        title.contains("can't be reached", ignoreCase = true) ||
        title.contains("ERR_INTERNET_DISCONNECTED", ignoreCase = true) ||
        title.contains("ERR_NAME_NOT_RESOLVED", ignoreCase = true) ||
        title.contains("ERR_CONNECTION", ignoreCase = true) ||
        title.contains("ERR_TIMED_OUT", ignoreCase = true) ||
        title.contains("No internet", ignoreCase = true)
}

internal fun resolveFailingUrl(
    requestUrl: String?,
    lastMainFrameUrl: String?,
): String? {
    val candidate = requestUrl?.takeIf { it.isNotBlank() && !isChromeErrorUrl(it) }
        ?: lastMainFrameUrl?.takeIf { it.isNotBlank() && !isChromeErrorUrl(it) }
    return candidate
}
