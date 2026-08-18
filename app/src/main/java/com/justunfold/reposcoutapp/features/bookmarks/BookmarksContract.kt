package com.justunfold.reposcoutapp.features.bookmarks

import com.justunfold.reposcoutapp.domain.model.RepositoryItem

data class BookmarksUiState(
    val savedRepositories: List<RepositoryItem> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface BookmarksIntent {
    data class RemoveBookmark(val id: Long) : BookmarksIntent
}
