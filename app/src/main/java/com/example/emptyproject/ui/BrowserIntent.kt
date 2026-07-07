package com.example.emptyproject.ui

import android.graphics.Bitmap

sealed interface BrowserIntent {
    data class SearchSubmitted(val query: String) : BrowserIntent
    data class OmniboxChanged(val text: String) : BrowserIntent
    data object GoHome : BrowserIntent
    data object OmniboxFocused : BrowserIntent
    data object OmniboxBlurred : BrowserIntent
    data object GoBack : BrowserIntent
    data object GoForward : BrowserIntent
    data object Reload : BrowserIntent
    data object StopLoading : BrowserIntent
    data object OpenTabSwitcher : BrowserIntent
    data object CloseTabSwitcher : BrowserIntent
    data class SwitchTab(val tabId: String) : BrowserIntent
    data class CloseTab(val tabId: String) : BrowserIntent
    data object NewTab : BrowserIntent
    data object RetryLoad : BrowserIntent
    data object LoadUrlConsumed : BrowserIntent
    data class PageStarted(val url: String) : BrowserIntent
    data class PageFinished(val url: String) : BrowserIntent
    data class ProgressChanged(val progress: Int) : BrowserIntent
    data class TitleChanged(val title: String) : BrowserIntent
    data class FaviconReceived(val favicon: Bitmap?) : BrowserIntent
    data class LoadFailed(val url: String) : BrowserIntent
    data class NavStateChanged(val canGoBack: Boolean, val canGoForward: Boolean) : BrowserIntent
}
