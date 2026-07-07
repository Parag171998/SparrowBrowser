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
    data class LoadUrlConsumed(val tabId: String) : BrowserIntent
    data class PageStarted(val tabId: String, val url: String) : BrowserIntent
    data class PageFinished(val tabId: String, val url: String) : BrowserIntent
    data class ProgressChanged(val tabId: String, val progress: Int) : BrowserIntent
    data class TitleChanged(val tabId: String, val title: String) : BrowserIntent
    data class FaviconReceived(val tabId: String, val favicon: Bitmap?) : BrowserIntent
    data class LoadFailed(val tabId: String, val url: String) : BrowserIntent
    data class NavStateChanged(val tabId: String, val canGoBack: Boolean, val canGoForward: Boolean) : BrowserIntent
    data class ThumbnailCaptured(val tabId: String, val thumbnail: Bitmap?) : BrowserIntent
}
