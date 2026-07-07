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
import com.example.emptyproject.webview.WebViewHolder

@Composable
fun BrowserWebView(
    state: BrowserUiState,
    onIntent: (BrowserIntent) -> Unit,
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

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { holder.webView },
    )
}
