package com.example.emptyproject.ui

import android.graphics.Bitmap
import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.model.Tab

enum class Screen {
    Browsing,
    TabSwitcher,
}

data class BrowserUiState(
    val tabs: List<Tab> = emptyList(),
    val activeTabId: String = "",
    val screen: Screen = Screen.Browsing,
    val omniboxText: String = "",
    val isOmniboxFocused: Boolean = false,
    val loadState: PageLoadState = PageLoadState.Idle,
    val loadProgress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val favicon: Bitmap? = null,
    val showError: Boolean = false,
    val errorUrl: String? = null,
    val pendingLoadUrl: String? = null,
)
