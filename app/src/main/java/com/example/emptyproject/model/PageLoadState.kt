package com.example.emptyproject.model

sealed interface PageLoadState {
    data object Idle : PageLoadState
    data object Loading : PageLoadState
    data object Loaded : PageLoadState
    data class Error(val url: String) : PageLoadState
}
