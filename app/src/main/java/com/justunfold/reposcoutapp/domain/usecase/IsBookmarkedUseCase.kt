package com.justunfold.reposcoutapp.domain.usecase

import com.justunfold.reposcoutapp.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class IsBookmarkedUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(id: Long): Flow<Boolean> {
        return bookmarkRepository.isBookmarked(id)
    }
}
