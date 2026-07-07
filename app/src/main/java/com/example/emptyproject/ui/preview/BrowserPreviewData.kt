package com.example.emptyproject.ui.preview

import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.model.Tab
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.Screen

object BrowserPreviewData {
    private const val TAB_ID = "preview-tab-1"
    private const val TAB_ID_2 = "preview-tab-2"

    val home = BrowserUiState(
        tabs = listOf(
            Tab(
                id = TAB_ID,
                isNewTab = true,
            ),
        ),
        activeTabId = TAB_ID,
        screen = Screen.Browsing,
    )

    val browsing = BrowserUiState(
        tabs = listOf(
            Tab(
                id = TAB_ID,
                title = "Wikipedia",
                url = "https://en.wikipedia.org",
                loadState = PageLoadState.Loaded,
                isNewTab = false,
                canGoBack = true,
                canGoForward = false,
            ),
        ),
        activeTabId = TAB_ID,
        screen = Screen.Browsing,
    )

    val browsingLoading = BrowserUiState(
        tabs = listOf(
            Tab(
                id = TAB_ID,
                title = "News",
                url = "https://news.example.com",
                loadState = PageLoadState.Loading,
                loadProgress = 45,
                isNewTab = false,
                canGoBack = true,
            ),
        ),
        activeTabId = TAB_ID,
        screen = Screen.Browsing,
    )

    val browsingError = BrowserUiState(
        tabs = listOf(
            Tab(
                id = TAB_ID,
                url = "https://invalid.example.com",
                loadState = PageLoadState.Error("https://invalid.example.com"),
                showError = true,
                errorUrl = "https://invalid.example.com",
                isNewTab = false,
            ),
        ),
        activeTabId = TAB_ID,
        screen = Screen.Browsing,
    )

    val tabSwitcher = BrowserUiState(
        tabs = listOf(
            Tab(
                id = TAB_ID,
                title = "Wikipedia",
                url = "https://en.wikipedia.org",
                loadState = PageLoadState.Loaded,
                isNewTab = false,
            ),
            Tab(
                id = TAB_ID_2,
                title = "Google Search",
                url = "https://www.google.com/search?q=kotlin",
                loadState = PageLoadState.Loaded,
                isNewTab = false,
            ),
            Tab(
                id = "preview-tab-3",
                isNewTab = true,
            ),
        ),
        activeTabId = TAB_ID,
        screen = Screen.TabSwitcher,
    )
}
