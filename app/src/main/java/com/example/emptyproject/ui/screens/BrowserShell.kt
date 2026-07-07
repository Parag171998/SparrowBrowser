package com.example.emptyproject.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.BrowserViewModel
import com.example.emptyproject.ui.Screen
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
        modifier = modifier,
    )
}

@Composable
fun BrowserShellContent(
    state: BrowserUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = when (state.screen) {
                Screen.NewTab -> "Sparrow Browser"
                Screen.Browsing -> browsingLabel(state)
                Screen.TabSwitcher -> "Tab Switcher (${state.tabs.size} tabs)"
            },
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

private fun browsingLabel(state: BrowserUiState): String {
    return when {
        state.showError -> "Error"
        state.loadState is PageLoadState.Loading -> "Loading ${state.loadProgress}%"
        else -> state.omniboxText.ifBlank { "Browsing" }
    }
}

@Preview(name = "New Tab", showBackground = true)
@Composable
private fun BrowserShellNewTabPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(state = BrowserPreviewData.newTab)
    }
}

@Preview(name = "Browsing - Loaded", showBackground = true)
@Composable
private fun BrowserShellBrowsingPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(state = BrowserPreviewData.browsing)
    }
}

@Preview(name = "Browsing - Loading", showBackground = true)
@Composable
private fun BrowserShellLoadingPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(state = BrowserPreviewData.browsingLoading)
    }
}

@Preview(name = "Browsing - Error", showBackground = true)
@Composable
private fun BrowserShellErrorPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(state = BrowserPreviewData.browsingError)
    }
}

@Preview(name = "Tab Switcher", showBackground = true)
@Composable
private fun BrowserShellTabSwitcherPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(state = BrowserPreviewData.tabSwitcher)
    }
}
