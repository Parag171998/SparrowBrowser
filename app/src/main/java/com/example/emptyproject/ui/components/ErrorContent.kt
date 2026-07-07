package com.example.emptyproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.theme.SparrowBrowserTheme

@Composable
fun ErrorContent(
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
    headline: String = stringResource(R.string.error_headline),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        BrowserIcon(
            resId = R.drawable.ic_error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { onIntent(BrowserIntent.RetryLoad) }) {
            Text(text = stringResource(R.string.error_retry))
        }
    }
}

@Preview(name = "Error Content", showBackground = true)
@Composable
private fun ErrorContentPreview() {
    SparrowBrowserTheme {
        ErrorContent(onIntent = {})
    }
}
