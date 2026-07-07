package com.example.emptyproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.emptyproject.ui.BrowserViewModel
import com.example.emptyproject.ui.preview.BrowserPreviewData
import com.example.emptyproject.ui.screens.BrowserShell
import com.example.emptyproject.ui.screens.BrowserShellContent
import com.example.emptyproject.ui.theme.SparrowBrowserTheme

@Composable
fun SparrowNavHost(
    modifier: Modifier = Modifier,
    viewModel: BrowserViewModel = hiltViewModel(),
) {
    BrowserShell(
        modifier = modifier,
        viewModel = viewModel,
    )
}

@Preview(name = "Nav Host - New Tab", showBackground = true, showSystemUi = true)
@Composable
private fun SparrowNavHostPreview() {
    SparrowBrowserTheme {
        BrowserShellContent(
            state = BrowserPreviewData.newTab,
            onIntent = {},
        )
    }
}
