package com.example.emptyproject.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.BrowserViewModel
import com.example.emptyproject.ui.Screen
import com.example.emptyproject.ui.WebViewCommand
import com.example.emptyproject.ui.activeCanGoBack
import com.example.emptyproject.ui.activeOmniboxText
import com.example.emptyproject.ui.activeShowError
import com.example.emptyproject.ui.components.BrowserBottomBar
import com.example.emptyproject.ui.components.BrowserTopBar
import com.example.emptyproject.ui.components.ErrorContent
import com.example.emptyproject.ui.components.HomeStartContent
import com.example.emptyproject.ui.components.MultiTabWebViewLayer
import com.example.emptyproject.ui.showHomeStart
import com.example.emptyproject.ui.components.TabSwitcherScreen
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

    val backEnabled = remember(state.screen, state.activeTabId, state.tabs) {
        when (state.screen) {
            Screen.TabSwitcher -> true
            Screen.Browsing -> state.activeCanGoBack()
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
    val hasWebView = state.tabs.any { !it.isNewTab }

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
            if (LocalInspectionMode.current && !state.showHomeStart()) {
                BrowsingPreviewPlaceholder(state = state, modifier = Modifier.fillMaxSize())
            } else if (hasWebView) {
                MultiTabWebViewLayer(
                    tabs = state.tabs,
                    activeTabId = state.activeTabId,
                    screen = state.screen,
                    onIntent = onIntent,
                    webViewCommands = webViewCommands,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (state.activeShowError() && state.screen == Screen.Browsing) {
                ErrorContent(
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (state.showHomeStart()) {
                HomeStartContent(
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (state.screen == Screen.TabSwitcher) {
                TabSwitcherScreen(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
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
            text = when {
                state.screen == Screen.TabSwitcher -> "Tab Switcher (${state.tabs.size})"
                state.showHomeStart() -> "Home Start"
                state.activeShowError() -> "Error"
                else -> state.activeOmniboxText().ifBlank { "WebView" }
            },
            style = MaterialTheme.typography.bodyLarge,
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

@Preview(name = "Shell - Error", showBackground = true, showSystemUi = true)
@Composable
private fun BrowserShellErrorPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.browsingError,
            onIntent = {},
        )
    }
}

@Preview(name = "Shell - Loading", showBackground = true, showSystemUi = true)
@Composable
private fun BrowserShellLoadingPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.browsingLoading,
            onIntent = {},
        )
    }
}

@Preview(name = "Shell - Home", showBackground = true, showSystemUi = true)
@Composable
private fun BrowserShellHomePreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.home,
            onIntent = {},
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
