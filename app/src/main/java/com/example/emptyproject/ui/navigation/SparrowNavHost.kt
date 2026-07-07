package com.example.emptyproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.emptyproject.ui.BrowserViewModel
import com.example.emptyproject.ui.screens.BrowserShell

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
