package com.example.emptyproject.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.emptyproject.model.Tab
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.Screen
import com.example.emptyproject.ui.WebViewCommand
import com.example.emptyproject.webview.WebViewHolder
import com.example.emptyproject.webview.captureWebViewThumbnail
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun MultiTabWebViewLayer(
    tabs: List<Tab>,
    activeTabId: String,
    screen: Screen,
    onIntent: (BrowserIntent) -> Unit,
    webViewCommands: SharedFlow<WebViewCommand>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val browsedTabs = tabs.filter { !it.isNewTab }

    browsedTabs.forEach { tab ->
        val holder = remember(tab.id) {
            WebViewHolder(context, tab.id, onIntent)
        }
        val isActive = tab.id == activeTabId
        val isVisible = isActive && screen == Screen.Browsing

        LaunchedEffect(tab.id, tab.pendingLoadUrl) {
            val url = tab.pendingLoadUrl ?: return@LaunchedEffect
            holder.webView.loadUrl(url)
            onIntent(BrowserIntent.LoadUrlConsumed(tab.id))
        }

        LaunchedEffect(tab.id, isActive, webViewCommands) {
            if (!isActive) return@LaunchedEffect
            webViewCommands.collect { command ->
                when (command) {
                    WebViewCommand.GoBack -> if (holder.webView.canGoBack()) holder.webView.goBack()
                    WebViewCommand.GoForward -> if (holder.webView.canGoForward()) holder.webView.goForward()
                    WebViewCommand.Reload -> holder.webView.reload()
                    WebViewCommand.StopLoading -> holder.webView.stopLoading()
                }
            }
        }

        LaunchedEffect(screen, tab.id) {
            if (screen == Screen.TabSwitcher) {
                val thumbnail = captureWebViewThumbnail(holder.webView)
                onIntent(BrowserIntent.ThumbnailCaptured(tab.id, thumbnail))
            }
        }

        AndroidView(
            modifier = modifier
                .fillMaxSize()
                .zIndex(if (isVisible) 1f else 0f)
                .alpha(if (isVisible) 1f else 0f),
            factory = { holder.webView },
        )
    }
}
