package com.justunfold.reposcoutapp.features.detail

import com.justunfold.reposcoutapp.domain.model.RepositoryItem

data class DetailUiState(
    val repository: RepositoryItem? = null,
    val isBookmarked: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRateLimit: Boolean = false
)

sealed interface DetailIntent {
    data class LoadDetails(val owner: String, val repo: String) : DetailIntent
    data object ToggleBookmark : DetailIntent
    data object Retry : DetailIntent
}
