package com.example.emptyproject.webview

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.emptyproject.ui.BrowserIntent

class WebViewHolder(
    context: Context,
    private val tabId: String,
    private val onIntent: (BrowserIntent) -> Unit,
) {
    val webView: WebView = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        @Suppress("DEPRECATION")
        settings.databaseEnabled = true

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                url?.let { onIntent(BrowserIntent.PageStarted(tabId, it)) }
                onIntent(BrowserIntent.FaviconReceived(tabId, favicon))
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                url?.let { onIntent(BrowserIntent.PageFinished(tabId, it)) }
                onIntent(BrowserIntent.TitleChanged(tabId, view?.title.orEmpty()))
                onIntent(
                    BrowserIntent.NavStateChanged(
                        tabId = tabId,
                        canGoBack = view?.canGoBack() == true,
                        canGoForward = view?.canGoForward() == true,
                    ),
                )
            }

            @Suppress("DEPRECATION")
            @Deprecated("Deprecated in API 23")
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?,
            ) {
                if (!failingUrl.isNullOrBlank()) {
                    onIntent(BrowserIntent.LoadFailed(tabId, failingUrl))
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                if (request?.isForMainFrame == true) {
                    val url = request.url?.toString()
                    if (!url.isNullOrBlank()) {
                        onIntent(BrowserIntent.LoadFailed(tabId, url))
                    }
                }
            }
        }

        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                onIntent(BrowserIntent.ProgressChanged(tabId, newProgress))
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                onIntent(BrowserIntent.TitleChanged(tabId, title.orEmpty()))
            }
        }
    }
}

fun captureWebViewThumbnail(webView: WebView): Bitmap? {
    if (webView.width <= 0 || webView.height <= 0) return null
    return try {
        val bitmap = Bitmap.createBitmap(webView.width, webView.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        webView.draw(canvas)
        bitmap
    } catch (_: Exception) {
        null
    }
}
