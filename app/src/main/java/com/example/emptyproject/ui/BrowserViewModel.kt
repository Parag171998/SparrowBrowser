package com.example.emptyproject.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.os.Bundle
import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.model.Tab
import com.example.emptyproject.util.BrowserConstants
import com.example.emptyproject.util.resolveInputToUrl
import com.example.emptyproject.webview.isChromeErrorUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BrowserViewModel @Inject constructor() : ViewModel() {

    private val initialTabId = UUID.randomUUID().toString()

    private val _state = MutableStateFlow(
        BrowserUiState(
            tabs = listOf(createHomeTab(initialTabId)),
            activeTabId = initialTabId,
            screen = Screen.Browsing,
        ),
    )
    val state: StateFlow<BrowserUiState> = _state.asStateFlow()

    private val _webViewCommands = MutableSharedFlow<WebViewCommand>(extraBufferCapacity = 1)
    val webViewCommands: SharedFlow<WebViewCommand> = _webViewCommands.asSharedFlow()

    private val savedWebViewStates = mutableMapOf<String, Bundle>()

    fun getSavedWebViewState(tabId: String): Bundle? = savedWebViewStates[tabId]

    fun saveWebViewState(tabId: String, bundle: Bundle) {
        savedWebViewStates[tabId] = bundle
    }

    private fun clearWebViewState(tabId: String) {
        savedWebViewStates.remove(tabId)
    }

    fun onIntent(intent: BrowserIntent) {
        when (intent) {
            is BrowserIntent.SearchSubmitted -> handleSearchSubmitted(intent.query)
            is BrowserIntent.OmniboxChanged -> updateTab(_state.value.activeTabId) { it.copy(url = intent.text) }
            is BrowserIntent.OmniboxFocused -> _state.update { it.copy(isOmniboxFocused = true) }
            is BrowserIntent.OmniboxBlurred -> _state.update { it.copy(isOmniboxFocused = false) }
            is BrowserIntent.GoHome -> handleGoHome()
            is BrowserIntent.GoBack -> emitWebViewCommand(WebViewCommand.GoBack)
            is BrowserIntent.GoForward -> emitWebViewCommand(WebViewCommand.GoForward)
            is BrowserIntent.Reload -> handleReload()
            is BrowserIntent.StopLoading -> handleStopLoading()
            is BrowserIntent.RetryLoad -> handleRetryLoad()
            is BrowserIntent.OpenTabSwitcher -> _state.update { it.copy(screen = Screen.TabSwitcher) }
            is BrowserIntent.CloseTabSwitcher -> {
                _state.update { current ->
                    if (current.screen == Screen.TabSwitcher) {
                        current.copy(screen = Screen.Browsing)
                    } else {
                        current
                    }
                }
            }
            is BrowserIntent.NewTab -> handleNewTab()
            is BrowserIntent.SwitchTab -> handleSwitchTab(intent.tabId)
            is BrowserIntent.CloseTab -> handleCloseTab(intent.tabId)
            is BrowserIntent.LoadUrlConsumed -> {
                updateTab(intent.tabId) { it.copy(pendingLoadUrl = null) }
            }
            is BrowserIntent.PageStarted -> handlePageStarted(intent.tabId, intent.url)
            is BrowserIntent.PageFinished -> handlePageFinished(intent.tabId, intent.url)
            is BrowserIntent.ProgressChanged -> handleProgressChanged(intent.tabId, intent.progress)
            is BrowserIntent.TitleChanged -> handleTitleChanged(intent.tabId, intent.title)
            is BrowserIntent.FaviconReceived -> {
                updateTab(intent.tabId) { it.copy(favicon = intent.favicon) }
            }
            is BrowserIntent.LoadFailed -> handleLoadFailed(intent.tabId, intent.url)
            is BrowserIntent.NavStateChanged -> {
                updateTab(intent.tabId) {
                    it.copy(canGoBack = intent.canGoBack, canGoForward = intent.canGoForward)
                }
            }
            is BrowserIntent.ThumbnailCaptured -> {
                updateTab(intent.tabId) { it.copy(thumbnail = intent.thumbnail) }
            }
        }
    }

    fun handleSystemBack(): Boolean {
        val current = _state.value
        return when {
            current.screen == Screen.TabSwitcher -> {
                onIntent(BrowserIntent.CloseTabSwitcher)
                true
            }
            current.screen == Screen.Browsing && current.activeCanGoBack() -> {
                onIntent(BrowserIntent.GoBack)
                true
            }
            else -> false
        }
    }

    private fun handleNewTab() {
        if (_state.value.tabs.size >= BrowserConstants.MAX_TABS) return
        val tabId = UUID.randomUUID().toString()
        _state.update {
            it.copy(
                tabs = it.tabs + createHomeTab(tabId),
                activeTabId = tabId,
                screen = Screen.Browsing,
                isOmniboxFocused = false,
            )
        }
    }

    private fun handleSwitchTab(tabId: String) {
        if (_state.value.tabs.none { it.id == tabId }) return
        _state.update {
            it.copy(
                activeTabId = tabId,
                screen = Screen.Browsing,
                isOmniboxFocused = false,
            )
        }
    }

    private fun handleCloseTab(tabId: String) {
        val current = _state.value
        val remaining = current.tabs.filter { it.id != tabId }
        if (remaining.isEmpty()) {
            savedWebViewStates.clear()
            val newTabId = UUID.randomUUID().toString()
            _state.value = BrowserUiState(
                tabs = listOf(createHomeTab(newTabId)),
                activeTabId = newTabId,
                screen = Screen.Browsing,
            )
            return
        }
        val newActiveId = if (tabId == current.activeTabId) {
            val index = current.tabs.indexOfFirst { it.id == tabId }
            remaining.getOrElse(index.coerceAtMost(remaining.lastIndex)) { remaining.first() }.id
        } else {
            current.activeTabId
        }
        clearWebViewState(tabId)
        _state.update {
            it.copy(
                tabs = remaining,
                activeTabId = newActiveId,
                screen = if (it.screen == Screen.TabSwitcher) Screen.TabSwitcher else Screen.Browsing,
            )
        }
    }

    private fun handleGoHome() {
        val tabId = _state.value.activeTabId
        clearWebViewState(tabId)
        updateTab(tabId) {
            Tab(id = it.id, isNewTab = true)
        }
        _state.update { it.copy(screen = Screen.Browsing, isOmniboxFocused = false) }
    }

    private fun handleSearchSubmitted(query: String) {
        val url = resolveInputToUrl(query)
        if (url.isBlank()) return
        val tabId = _state.value.activeTabId
        updateTab(tabId) {
            it.copy(
                isNewTab = false,
                url = url,
                loadState = PageLoadState.Loading,
                pendingLoadUrl = url,
                loadProgress = 0,
                showError = false,
                errorUrl = null,
            )
        }
        _state.update { it.copy(screen = Screen.Browsing, isOmniboxFocused = false) }
    }

    private fun handleReload() {
        val tab = _state.value.activeTab() ?: return
        if (tab.showError && !tab.errorUrl.isNullOrBlank()) {
            handleRetryLoad()
        } else {
            emitWebViewCommand(WebViewCommand.Reload)
        }
    }

    private fun handleRetryLoad() {
        val tab = _state.value.activeTab() ?: return
        val url = tab.errorUrl?.takeIf { it.isNotBlank() } ?: tab.url.takeIf { it.isNotBlank() } ?: return
        updateTab(tab.id) {
            it.copy(
                url = url,
                loadState = PageLoadState.Loading,
                pendingLoadUrl = url,
                loadProgress = 0,
                showError = false,
                errorUrl = null,
            )
        }
        _state.update { it.copy(isOmniboxFocused = false) }
    }

    private fun handlePageStarted(tabId: String, url: String) {
        if (isChromeErrorUrl(url)) return
        updateTab(tabId) {
            it.copy(
                url = url,
                loadState = PageLoadState.Loading,
                isNewTab = false,
                loadProgress = 0,
                showError = false,
                errorUrl = null,
            )
        }
        if (tabId == _state.value.activeTabId) {
            _state.update { it.copy(isOmniboxFocused = false) }
        }
    }

    private fun handlePageFinished(tabId: String, url: String) {
        if (isChromeErrorUrl(url)) return
        val tab = _state.value.tabs.find { it.id == tabId } ?: return
        if (tab.showError || tab.loadState is PageLoadState.Error) return
        updateTab(tabId) {
            it.copy(url = url, loadState = PageLoadState.Loaded, loadProgress = 100)
        }
        if (tabId == _state.value.activeTabId) {
            _state.update { it.copy(isOmniboxFocused = false) }
        }
    }

    private fun handleStopLoading() {
        val tabId = _state.value.activeTabId
        emitWebViewCommand(WebViewCommand.StopLoading)
        updateTab(tabId) { tab ->
            if (tab.loadState is PageLoadState.Loading) {
                tab.copy(loadState = PageLoadState.Loaded, loadProgress = 0)
            } else {
                tab
            }
        }
    }

    private fun handleProgressChanged(tabId: String, progress: Int) {
        val tab = _state.value.tabs.find { it.id == tabId } ?: return
        if (tab.showError || tab.loadState is PageLoadState.Error) return
        updateTab(tabId) {
            it.copy(
                loadProgress = progress,
                loadState = when {
                    progress >= 100 -> PageLoadState.Loaded
                    progress > 0 -> PageLoadState.Loading
                    else -> it.loadState
                },
            )
        }
    }

    private fun handleTitleChanged(tabId: String, title: String) {
        if (title.isBlank()) return
        updateTab(tabId) { it.copy(title = title) }
    }

    private fun handleLoadFailed(tabId: String, url: String) {
        updateTab(tabId) {
            it.copy(
                url = url,
                loadState = PageLoadState.Error(url),
                loadProgress = 0,
                showError = true,
                errorUrl = url,
            )
        }
        if (tabId == _state.value.activeTabId) {
            _state.update { it.copy(isOmniboxFocused = false) }
        }
    }

    private fun updateTab(tabId: String, transform: (Tab) -> Tab) {
        _state.update { current ->
            current.copy(
                tabs = current.tabs.map { tab ->
                    if (tab.id == tabId) transform(tab) else tab
                },
            )
        }
    }

    private fun emitWebViewCommand(command: WebViewCommand) {
        viewModelScope.launch {
            _webViewCommands.emit(command)
        }
    }

    private fun createHomeTab(tabId: String): Tab {
        return Tab(id = tabId, isNewTab = true)
    }
}
