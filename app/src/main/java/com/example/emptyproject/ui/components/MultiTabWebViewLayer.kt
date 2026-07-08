package com.example.emptyproject.ui.components

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    getSavedWebViewState: (String) -> Bundle? = { null },
    onSaveWebViewState: (String, Bundle) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val browsedTabs = tabs.filter { !it.isNewTab }

    browsedTabs.forEach { tab ->
        val holder = remember(tab.id) {
            WebViewHolder(context, tab.id, onIntent)
        }
        val isActive = tab.id == activeTabId
        val isVisible = isActive && screen == Screen.Browsing && !tab.showError

        LaunchedEffect(tab.id, tab.pendingLoadUrl) {
            val url = tab.pendingLoadUrl ?: return@LaunchedEffect
            holder.trackLoadUrl(url)
            holder.webView.loadUrl(url)
            onIntent(BrowserIntent.LoadUrlConsumed(tab.id))
        }

        LaunchedEffect(tab.id, tab.url, tab.isNewTab, tab.pendingLoadUrl) {
            if (tab.isNewTab || tab.pendingLoadUrl != null) return@LaunchedEffect

            val savedState = getSavedWebViewState(tab.id)
            if (savedState != null && holder.restoreState(savedState)) {
                return@LaunchedEffect
            }

            val webView = holder.webView
            val currentUrl = webView.url
            if ((currentUrl.isNullOrBlank() || currentUrl == "about:blank") && tab.url.isNotBlank()) {
                holder.trackLoadUrl(tab.url)
                webView.loadUrl(tab.url)
            }
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

        DisposableEffect(tab.id, isActive, lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> if (isActive) holder.webView.onResume()
                    Lifecycle.Event.ON_PAUSE -> holder.webView.onPause()
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && isActive) {
                holder.webView.onResume()
            }
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                holder.webView.onPause()
                onSaveWebViewState(tab.id, holder.saveState())
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
