package com.justunfold.reposcoutapp.features.explore

import com.justunfold.reposcoutapp.domain.model.RepositoryItem

data class ExploreUiState(
    val repositories: List<RepositoryItem> = emptyList(),
    val bookmarkedIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val isRateLimit: Boolean = false,
    val currentPage: Int = 1,
    val canPaginate: Boolean = true
)

sealed interface ExploreIntent {
    data object Refresh : ExploreIntent
    data object LoadMore : ExploreIntent
    data object Retry : ExploreIntent
    data class ToggleBookmark(val repository: RepositoryItem) : ExploreIntent
}
