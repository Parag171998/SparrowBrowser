package com.example.emptyproject.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.compose.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.BrowserViewModel
import com.example.emptyproject.ui.Screen
import com.example.emptyproject.ui.WebViewCommand
import com.example.emptyproject.ui.components.BrowserBottomBar
import com.example.emptyproject.ui.components.BrowserTopBar
import com.example.emptyproject.ui.components.BrowserWebView
import com.example.emptyproject.ui.preview.BrowserPreviewData
import com.example.emptyproject.ui.theme.SparrowBrowserTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun BrowserShell(
    modifier: Modifier = Modifier,
    viewModel: BrowserViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val backEnabled = remember(state.screen, state.canGoBack) {
        when (state.screen) {
            Screen.TabSwitcher -> true
            Screen.Browsing -> state.canGoBack
        }
    }

    BackHandler(enabled = backEnabled) {
        viewModel.handleSystemBack()
    }

    BrowserShellContent(
        state = state,
        onIntent = viewModel::onIntent,
        webViewCommands = viewModel.webViewCommands,
        modifier = modifier,
    )
}

@Composable
fun BrowserShellContent(
    state: BrowserUiState,
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
    webViewCommands: SharedFlow<WebViewCommand> = MutableSharedFlow(),
) {
    val activeTab = state.tabs.find { it.id == state.activeTabId }
    val hasWebView = activeTab?.isNewTab == false

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            BrowserTopBar(state = state, onIntent = onIntent)
        },
        bottomBar = {
            BrowserBottomBar(state = state, onIntent = onIntent)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (state.screen) {
                Screen.Browsing -> {
                    if (LocalInspectionMode.current) {
                        BrowsingPreviewPlaceholder(
                            state = state,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else if (hasWebView) {
                        BrowserWebView(
                            state = state,
                            onIntent = onIntent,
                            webViewCommands = webViewCommands,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                Screen.TabSwitcher -> {
                    if (hasWebView && !LocalInspectionMode.current) {
                        BrowserWebView(
                            state = state,
                            onIntent = onIntent,
                            webViewCommands = webViewCommands,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Tab Switcher (${state.tabs.size} tabs)",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrowsingPreviewPlaceholder(
    state: BrowserUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = state.omniboxText.ifBlank { "WebView" },
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(name = "Shell - Browsing", showBackground = true, showSystemUi = true)
@Composable
private fun BrowserShellBrowsingPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.browsing,
            onIntent = {},
        )
    }
}

@Preview(name = "Shell - Tab Switcher", showBackground = true, showSystemUi = true)
@Composable
private fun BrowserShellTabSwitcherPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.tabSwitcher,
            onIntent = {},
        )
    }
}
