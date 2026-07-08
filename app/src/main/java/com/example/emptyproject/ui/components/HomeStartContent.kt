package com.example.emptyproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.emptyproject.R
import com.example.emptyproject.model.DefaultShortcuts
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.theme.BrowserBackground
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.SparrowBrowserTheme

@Composable
fun HomeStartContent(
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrowserBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrowserIcon(
            resId = R.drawable.ic_sparrow_logo,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(28.dp))
        StartSearchBox(
            onIntent = onIntent,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(Dimens.homeShortcutTopSpacing))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        ) {
            DefaultShortcuts.items.forEach { shortcut ->
                ShortcutCard(
                    shortcut = shortcut,
                    onIntent = onIntent,
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.home_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(name = "Home Start", showBackground = true, showSystemUi = true)
@Composable
private fun HomeStartContentPreview() {
    SparrowBrowserTheme {
        HomeStartContent(onIntent = {})
    }
}
