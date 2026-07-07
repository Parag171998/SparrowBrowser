package com.example.emptyproject.model

data class Tab(
    val id: String,
    val title: String = "",
    val url: String = "",
    val loadState: PageLoadState = PageLoadState.Idle,
    val isNewTab: Boolean = true,
)
