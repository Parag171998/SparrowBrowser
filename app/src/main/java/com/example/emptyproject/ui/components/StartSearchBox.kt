package com.example.emptyproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.emptyproject.R
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.OmniboxBackground
import com.example.emptyproject.ui.theme.OmniboxBorder
import com.example.emptyproject.ui.theme.SparrowBrowserTheme

@Composable
fun StartSearchBox(
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val shape = RoundedCornerShape(28.dp)

    fun submit() {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        focusManager.clearFocus()
        keyboardController?.hide()
        onIntent(BrowserIntent.SearchSubmitted(trimmed))
        query = ""
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.startSearchBoxHeight)
            .background(OmniboxBackground, shape)
            .border(width = 1.dp, color = OmniboxBorder, shape = shape)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrowserIcon(
            resId = R.drawable.ic_outline_search,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
        )
        BasicTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { submit() }),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.start_search_placeholder),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                }
                innerTextField()
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StartSearchBoxPreview() {
    SparrowBrowserTheme {
        StartSearchBox(
            onIntent = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}
