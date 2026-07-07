package com.example.emptyproject.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.WebViewCommand
import com.example.emptyproject.webview.WebViewHolder
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun BrowserWebView(
    state: BrowserUiState,
    onIntent: (BrowserIntent) -> Unit,
    webViewCommands: SharedFlow<WebViewCommand>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val holder = remember(state.activeTabId) {
        WebViewHolder(context, onIntent)
    }

    LaunchedEffect(state.pendingLoadUrl) {
        val url = state.pendingLoadUrl ?: return@LaunchedEffect
        holder.webView.loadUrl(url)
        onIntent(BrowserIntent.LoadUrlConsumed)
    }

    LaunchedEffect(holder, webViewCommands) {
        webViewCommands.collect { command ->
            when (command) {
                WebViewCommand.GoBack -> if (holder.webView.canGoBack()) holder.webView.goBack()
                WebViewCommand.GoForward -> if (holder.webView.canGoForward()) holder.webView.goForward()
                WebViewCommand.Reload -> holder.webView.reload()
                WebViewCommand.StopLoading -> holder.webView.stopLoading()
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { holder.webView },
    )
}
