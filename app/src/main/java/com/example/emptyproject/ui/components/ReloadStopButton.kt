package com.example.emptyproject.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.SparrowBrowserTheme

@Composable
fun ReloadStopButton(
    isLoading: Boolean,
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = {
            if (isLoading) {
                onIntent(BrowserIntent.StopLoading)
            } else {
                onIntent(BrowserIntent.Reload)
            }
        },
        modifier = modifier.size(Dimens.toolbarIconButtonSize),
    ) {
        Icon(
            imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
            contentDescription = if (isLoading) "Stop loading" else "Reload",
            modifier = Modifier.size(Dimens.toolbarIconSize),
        )
    }
}

@Preview(name = "Reload", showBackground = true)
@Composable
private fun ReloadStopButtonIdlePreview() {
    SparrowBrowserTheme {
        ReloadStopButton(isLoading = false, onIntent = {})
    }
}

@Preview(name = "Stop", showBackground = true)
@Composable
private fun ReloadStopButtonLoadingPreview() {
    SparrowBrowserTheme {
        ReloadStopButton(isLoading = true, onIntent = {})
    }
}
