package com.justunfold.reposcoutapp.domain.repository

import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    suspend fun bookmarkRepository(repository: RepositoryItem)
    suspend fun removeBookmark(id: Long)
    fun getAllSavedRepositories(): Flow<List<RepositoryItem>>
    fun isBookmarked(id: Long): Flow<Boolean>
    suspend fun getSavedRepository(owner: String, repo: String): RepositoryItem?
}
