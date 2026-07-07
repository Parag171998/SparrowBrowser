package com.example.emptyproject.model

import android.graphics.Bitmap

data class Tab(
    val id: String,
    val title: String = "",
    val url: String = "",
    val loadState: PageLoadState = PageLoadState.Idle,
    val isNewTab: Boolean = true,
    val loadProgress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val favicon: Bitmap? = null,
    val showError: Boolean = false,
    val errorUrl: String? = null,
    val pendingLoadUrl: String? = null,
    val thumbnail: Bitmap? = null,
)
