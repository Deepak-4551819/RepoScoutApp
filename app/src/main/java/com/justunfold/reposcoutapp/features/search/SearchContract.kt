package com.justunfold.reposcoutapp.features.search

import com.justunfold.reposcoutapp.domain.model.RepositoryItem

enum class SearchFilter(val displayName: String, val querySuffix: String) {
    ALL("All", ""),
    KOTLIN("Kotlin", " language:kotlin"),
    JAVA("Java", " language:java"),
    COMPOSE("Compose", " compose in:name,description")
}

data class SearchUiState(
    val query: String = "",
    val activeFilter: SearchFilter = SearchFilter.ALL,
    val repositories: List<RepositoryItem> = emptyList(),
    val bookmarkedIds: Set<Long> = emptySet(),
    val totalCount: Int = 0,
    val isSearching: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val isRateLimit: Boolean = false,
    val currentPage: Int = 1,
    val canPaginate: Boolean = false
)

sealed interface SearchIntent {
    data class QueryChanged(val newQuery: String) : SearchIntent
    data class FilterSelected(val filter: SearchFilter) : SearchIntent
    data object ClearQuery : SearchIntent
    data object LoadMore : SearchIntent
    data object Retry : SearchIntent
    data class ToggleBookmark(val repository: RepositoryItem) : SearchIntent
}
