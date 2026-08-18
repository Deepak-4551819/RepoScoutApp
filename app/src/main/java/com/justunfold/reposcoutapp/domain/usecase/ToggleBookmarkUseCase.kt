package com.justunfold.reposcoutapp.domain.usecase

import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.BookmarkRepository

class ToggleBookmarkUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(repository: RepositoryItem, currentlyBookmarked: Boolean) {
        if (currentlyBookmarked) {
            bookmarkRepository.removeBookmark(repository.id)
        } else {
            bookmarkRepository.bookmarkRepository(repository)
        }
    }
}
