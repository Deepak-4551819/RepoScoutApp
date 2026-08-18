package com.justunfold.reposcoutapp.domain.usecase

import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class GetSavedRepositoriesUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<RepositoryItem>> {
        return bookmarkRepository.getAllSavedRepositories()
    }
}
