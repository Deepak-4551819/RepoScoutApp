package com.justunfold.reposcoutapp.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.usecase.GetRepositoryDetailUseCase
import com.justunfold.reposcoutapp.domain.usecase.IsBookmarkedUseCase
import com.justunfold.reposcoutapp.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getRepositoryDetailUseCase: GetRepositoryDetailUseCase,
    private val isBookmarkedUseCase: IsBookmarkedUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentOwner: String = ""
    private var currentRepoName: String = ""

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadDetails -> {
                currentOwner = intent.owner
                currentRepoName = intent.repo
                fetchRepositoryDetails()
            }
            is DetailIntent.ToggleBookmark -> toggleBookmark()
            is DetailIntent.Retry -> fetchRepositoryDetails()
        }
    }

    private fun fetchRepositoryDetails() {
        if (currentOwner.isBlank() || currentRepoName.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getRepositoryDetailUseCase(currentOwner, currentRepoName)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            repository = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    observeBookmarkStatus(result.data.id)
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

    private fun observeBookmarkStatus(repoId: Long) {
        isBookmarkedUseCase(repoId)
            .onEach { isBookmarked ->
                _uiState.update { it.copy(isBookmarked = isBookmarked) }
            }
            .launchIn(viewModelScope)
    }

    private fun toggleBookmark() {
        val repo = _uiState.value.repository ?: return
        val currentBookmarked = _uiState.value.isBookmarked
        viewModelScope.launch {
            toggleBookmarkUseCase(repo, currentBookmarked)
        }
    }
}
