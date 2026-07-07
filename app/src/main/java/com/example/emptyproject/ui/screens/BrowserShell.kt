package com.example.emptyproject.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emptyproject.ui.BrowserViewModel
import com.example.emptyproject.ui.Screen

@Composable
fun BrowserShell(
    modifier: Modifier = Modifier,
    viewModel: BrowserViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = when (state.screen) {
                Screen.NewTab -> "Sparrow Browser"
                Screen.Browsing -> "Browsing"
                Screen.TabSwitcher -> "Tab Switcher"
            },
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
