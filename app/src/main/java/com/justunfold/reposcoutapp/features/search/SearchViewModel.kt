package com.justunfold.reposcoutapp.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.usecase.GetSavedRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.SearchRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val getSavedRepositoriesUseCase: GetSavedRepositoriesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val searchTrigger = MutableStateFlow(Pair("", SearchFilter.ALL))

    init {
        observeBookmarks()
        observeSearchQuery()
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _uiState.update { it.copy(query = intent.newQuery) }
                searchTrigger.value = Pair(intent.newQuery, _uiState.value.activeFilter)
            }
            is SearchIntent.FilterSelected -> {
                if (_uiState.value.activeFilter != intent.filter) {
                    _uiState.update { it.copy(activeFilter = intent.filter) }
                    searchTrigger.value = Pair(_uiState.value.query, intent.filter)
                }
            }
            is SearchIntent.ClearQuery -> {
                _uiState.update {
                    it.copy(
                        query = "",
                        repositories = emptyList(),
                        isSearching = false,
                        errorMessage = null,
                        canPaginate = false
                    )
                }
                searchTrigger.value = Pair("", _uiState.value.activeFilter)
            }
            is SearchIntent.LoadMore -> loadMore()
            is SearchIntent.Retry -> {
                searchTrigger.value = Pair(_uiState.value.query, _uiState.value.activeFilter)
            }
            is SearchIntent.ToggleBookmark -> toggleBookmark(intent.repository)
        }
    }

    private fun observeBookmarks() {
        getSavedRepositoriesUseCase()
            .onEach { savedList ->
                _uiState.update { it.copy(bookmarkedIds = savedList.map { item -> item.id }.toSet()) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSearchQuery() {
        searchTrigger
            .debounce(400L)
            .distinctUntilChanged()
            .flatMapLatest { (rawQuery, filter) ->
                flow {
                    val trimmed = rawQuery.trim()
                    if (trimmed.isBlank()) {
                        _uiState.update {
                            it.copy(
                                repositories = emptyList(),
                                isSearching = false,
                                errorMessage = null,
                                canPaginate = false
                            )
                        }
                        emit(null)
                    } else {
                        _uiState.update { it.copy(isSearching = true, errorMessage = null) }
                        val fullQuery = "$trimmed${filter.querySuffix}"
                        val result = searchRepositoriesUseCase(query = fullQuery, page = 1)
                        emit(result)
                    }
                }
            }
            .onEach { result ->
                if (result == null) return@onEach
                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                repositories = result.data,
                                isSearching = false,
                                currentPage = 1,
                                canPaginate = result.data.size >= 20,
                                errorMessage = null
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                errorMessage = result.message,
                                isRateLimit = result.isRateLimit
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.canPaginate || state.isSearching || state.query.isBlank()) return

        val nextPage = state.currentPage + 1
        val fullQuery = "${state.query.trim()}${state.activeFilter.querySuffix}"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            when (val result = searchRepositoriesUseCase(query = fullQuery, page = nextPage)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            repositories = it.repositories + result.data,
                            isLoadingMore = false,
                            currentPage = nextPage,
                            canPaginate = result.data.size >= 20
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
            }
        }
    }

    private fun toggleBookmark(repository: RepositoryItem) {
        viewModelScope.launch {
            val isBookmarked = _uiState.value.bookmarkedIds.contains(repository.id)
            toggleBookmarkUseCase(repository, isBookmarked)
        }
    }
}
