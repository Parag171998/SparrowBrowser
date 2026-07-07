package com.example.emptyproject.ui

import androidx.lifecycle.ViewModel
import com.example.emptyproject.model.Tab
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class BrowserViewModel @Inject constructor() : ViewModel() {

    private val initialTabId = UUID.randomUUID().toString()

    private val _state = MutableStateFlow(
        BrowserUiState(
            tabs = listOf(Tab(id = initialTabId, isNewTab = true)),
            activeTabId = initialTabId,
            screen = Screen.NewTab,
        ),
    )
    val state: StateFlow<BrowserUiState> = _state.asStateFlow()

    fun onIntent(intent: BrowserIntent) {
        // Handled in later phases.
    }
}
