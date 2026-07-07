package com.example.emptyproject.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.emptyproject.R
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
        BrowserIcon(
            resId = if (isLoading) R.drawable.ic_stop else R.drawable.ic_reload,
            contentDescription = stringResource(
                if (isLoading) R.string.cd_stop else R.string.cd_reload,
            ),
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
