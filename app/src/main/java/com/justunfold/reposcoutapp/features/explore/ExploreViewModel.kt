package com.justunfold.reposcoutapp.features.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.usecase.GetExploreRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.GetSavedRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val getExploreRepositoriesUseCase: GetExploreRepositoriesUseCase,
    private val getSavedRepositoriesUseCase: GetSavedRepositoriesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        observeBookmarks()
        loadInitialRepositories()
    }

    fun onIntent(intent: ExploreIntent) {
        when (intent) {
            is ExploreIntent.Refresh -> refresh()
            is ExploreIntent.LoadMore -> loadMore()
            is ExploreIntent.Retry -> loadInitialRepositories()
            is ExploreIntent.ToggleBookmark -> toggleBookmark(intent.repository)
        }
    }

    private fun observeBookmarks() {
        getSavedRepositoriesUseCase()
            .onEach { savedList ->
                _uiState.update { it.copy(bookmarkedIds = savedList.map { item -> item.id }.toSet()) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadInitialRepositories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getExploreRepositoriesUseCase(page = 1)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            repositories = result.data,
                            isLoading = false,
                            currentPage = 1,
                            canPaginate = result.data.size >= 20,
                            errorMessage = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isRateLimit = result.isRateLimit
                        )
                    }
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            when (val result = getExploreRepositoriesUseCase(page = 1)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            repositories = result.data,
                            isRefreshing = false,
                            currentPage = 1,
                            canPaginate = result.data.size >= 20,
                            errorMessage = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = result.message,
                            isRateLimit = result.isRateLimit
                        )
                    }
                }
            }
        }
    }

    private fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.canPaginate || state.isLoading || state.isRefreshing) return

        val nextPage = state.currentPage + 1
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            when (val result = getExploreRepositoriesUseCase(page = nextPage)) {
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
                    _uiState.update {
                        it.copy(isLoadingMore = false)
                    }
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
