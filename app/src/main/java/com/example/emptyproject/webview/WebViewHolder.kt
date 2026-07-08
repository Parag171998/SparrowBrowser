package com.example.emptyproject.webview

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
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
    private var lastMainFrameUrl: String? = null
    private var loadFailedForCurrentNavigation = false

    val webView: WebView = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        @Suppress("DEPRECATION")
        settings.databaseEnabled = true

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (isChromeErrorUrl(url)) return
                lastMainFrameUrl = url
                loadFailedForCurrentNavigation = false
                url?.let { onIntent(BrowserIntent.PageStarted(tabId, it)) }
                onIntent(BrowserIntent.FaviconReceived(tabId, favicon))
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (isWebViewErrorPage(view, url)) {
                    reportLoadFailed(resolveFailingUrl(url, lastMainFrameUrl))
                    return
                }
                if (loadFailedForCurrentNavigation) return
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
                reportLoadFailed(resolveFailingUrl(failingUrl, lastMainFrameUrl))
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                if (request?.isForMainFrame != true) return
                reportLoadFailed(resolveFailingUrl(request.url?.toString(), lastMainFrameUrl))
            }
        }

        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (loadFailedForCurrentNavigation) return
                onIntent(BrowserIntent.ProgressChanged(tabId, newProgress))
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                if (loadFailedForCurrentNavigation) return
                onIntent(BrowserIntent.TitleChanged(tabId, title.orEmpty()))
                if (isWebViewErrorPage(view, view?.url)) {
                    reportLoadFailed(resolveFailingUrl(view?.url, lastMainFrameUrl))
                }
            }
        }
    }

    private fun reportLoadFailed(url: String?) {
        if (url.isNullOrBlank() || loadFailedForCurrentNavigation) return
        loadFailedForCurrentNavigation = true
        onIntent(BrowserIntent.LoadFailed(tabId, url))
    }

    fun trackLoadUrl(url: String) {
        if (url.isBlank() || isChromeErrorUrl(url)) return
        lastMainFrameUrl = url
        loadFailedForCurrentNavigation = false
    }

    fun saveState(): Bundle {
        val bundle = Bundle()
        webView.saveState(bundle)
        return bundle
    }

    fun restoreState(bundle: Bundle): Boolean {
        return webView.restoreState(bundle) != null
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
