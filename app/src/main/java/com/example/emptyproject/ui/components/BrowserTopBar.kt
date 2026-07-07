package com.example.emptyproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.activeFavicon
import com.example.emptyproject.ui.activeCanGoBack
import com.example.emptyproject.ui.activeCanGoForward
import com.example.emptyproject.ui.activeLoadProgress
import com.example.emptyproject.ui.activeLoadState
import com.example.emptyproject.ui.activeOmniboxText
import com.example.emptyproject.ui.preview.BrowserPreviewData
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.SparrowBrowserTheme
import com.example.emptyproject.ui.theme.OmniboxBorder
import com.example.emptyproject.ui.theme.ToolbarBackground

@Composable
fun BrowserTopBar(
    state: BrowserUiState,
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ToolbarBackground)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.toolbarHeight)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavButtons(
                canGoBack = state.activeCanGoBack(),
                canGoForward = state.activeCanGoForward(),
                onIntent = onIntent,
            )
            Omnibox(
                text = state.activeOmniboxText(),
                isFocused = state.isOmniboxFocused,
                favicon = state.activeFavicon(),
                onIntent = onIntent,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
            )
            ReloadStopButton(
                isLoading = state.activeLoadState() == PageLoadState.Loading,
                onIntent = onIntent,
            )
        }
        LoadProgressBar(
            loadState = state.activeLoadState(),
            progress = state.activeLoadProgress(),
        )
        HorizontalDivider(color = OmniboxBorder, thickness = 1.dp)
    }
}

@Preview(name = "Top Bar - Home", showBackground = true)
@Composable
private fun BrowserTopBarHomePreview() {
    SparrowBrowserTheme {
        BrowserTopBar(
            state = BrowserPreviewData.home,
            onIntent = {},
        )
    }
}

@Preview(name = "Top Bar - Browsing", showBackground = true)
@Composable
private fun BrowserTopBarBrowsingPreview() {
    SparrowBrowserTheme {
        BrowserTopBar(
            state = BrowserPreviewData.browsing,
            onIntent = {},
        )
    }
}

@Preview(name = "Top Bar - Error", showBackground = true)
@Composable
private fun BrowserTopBarErrorPreview() {
    SparrowBrowserTheme {
        BrowserTopBar(
            state = BrowserPreviewData.browsingError,
            onIntent = {},
        )
    }
}

@Preview(name = "Top Bar - Loading", showBackground = true)
@Composable
private fun BrowserTopBarLoadingPreview() {
    SparrowBrowserTheme {
        BrowserTopBar(
            state = BrowserPreviewData.browsingLoading,
            onIntent = {},
        )
    }
}
