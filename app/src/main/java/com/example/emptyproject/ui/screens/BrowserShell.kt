package com.example.emptyproject.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.BrowserViewModel
import com.example.emptyproject.ui.Screen
import com.example.emptyproject.ui.components.BrowserWebView
import com.example.emptyproject.ui.preview.BrowserPreviewData
import com.example.emptyproject.ui.theme.SparrowBrowserTheme

@Composable
fun BrowserShell(
    modifier: Modifier = Modifier,
    viewModel: BrowserViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BrowserShellContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
fun BrowserShellContent(
    state: BrowserUiState,
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.screen) {
        Screen.NewTab -> {
            NewTabScreen(
                onSearch = { onIntent(BrowserIntent.SearchSubmitted(it)) },
                modifier = modifier,
            )
        }

        Screen.Browsing -> {
            if (LocalInspectionMode.current) {
                BrowsingPreviewPlaceholder(state = state, modifier = modifier)
            } else {
                BrowserWebView(
                    state = state,
                    onIntent = onIntent,
                    modifier = modifier,
                )
            }
        }

        Screen.TabSwitcher -> {
            Box(
                modifier = modifier.fillMaxSize(),
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

@Composable
private fun BrowsingPreviewPlaceholder(
    state: BrowserUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = state.omniboxText.ifBlank { "WebView" },
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(name = "Shell - New Tab", showBackground = true)
@Composable
private fun BrowserShellNewTabPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.newTab,
            onIntent = {},
        )
    }
}

@Preview(name = "Shell - Browsing", showBackground = true)
@Composable
private fun BrowserShellBrowsingPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.browsing,
            onIntent = {},
        )
    }
}

@Preview(name = "Shell - Tab Switcher", showBackground = true)
@Composable
private fun BrowserShellTabSwitcherPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.tabSwitcher,
            onIntent = {},
        )
    }
}
