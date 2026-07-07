package com.example.emptyproject.ui

sealed interface WebViewCommand {
    data object GoBack : WebViewCommand
    data object GoForward : WebViewCommand
    data object Reload : WebViewCommand
}
