package com.example.emptyproject.ui

import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.model.Tab
import com.example.emptyproject.util.isHomePage

enum class Screen {
    Browsing,
    TabSwitcher,
}

data class BrowserUiState(
    val tabs: List<Tab> = emptyList(),
    val activeTabId: String = "",
    val screen: Screen = Screen.Browsing,
    val isOmniboxFocused: Boolean = false,
)

fun BrowserUiState.activeTab(): Tab? = tabs.find { it.id == activeTabId }

fun BrowserUiState.activeLoadState(): PageLoadState = activeTab()?.loadState ?: PageLoadState.Idle

fun BrowserUiState.activeLoadProgress(): Int = activeTab()?.loadProgress ?: 0

fun BrowserUiState.activeOmniboxText(): String = activeTab()?.url.orEmpty()

fun BrowserUiState.activeFavicon() = activeTab()?.favicon

fun BrowserUiState.activeCanGoBack(): Boolean = activeTab()?.canGoBack == true

fun BrowserUiState.activeCanGoForward(): Boolean = activeTab()?.canGoForward == true

fun BrowserUiState.activeShowError(): Boolean = activeTab()?.showError == true

fun BrowserUiState.showHomeStart(): Boolean {
    val tab = activeTab() ?: return false
    return screen == Screen.Browsing &&
        !tab.showError &&
        !isOmniboxFocused &&
        tab.url.isHomePage()
}
