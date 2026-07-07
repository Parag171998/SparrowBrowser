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
    private val onIntent: (BrowserIntent) -> Unit,
) {
    val webView: WebView = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        @Suppress("DEPRECATION")
        settings.databaseEnabled = true

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                url?.let { onIntent(BrowserIntent.PageStarted(it)) }
                onIntent(BrowserIntent.FaviconReceived(favicon))
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                url?.let { onIntent(BrowserIntent.PageFinished(it)) }
                onIntent(BrowserIntent.TitleChanged(view?.title.orEmpty()))
                onIntent(
                    BrowserIntent.NavStateChanged(
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
                    onIntent(BrowserIntent.LoadFailed(failingUrl))
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
                        onIntent(BrowserIntent.LoadFailed(url))
                    }
                }
            }
        }

        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                onIntent(BrowserIntent.ProgressChanged(newProgress))
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                onIntent(BrowserIntent.TitleChanged(title.orEmpty()))
            }
        }
    }
}
