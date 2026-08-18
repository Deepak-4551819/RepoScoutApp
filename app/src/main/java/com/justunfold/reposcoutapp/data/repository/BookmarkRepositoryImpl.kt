package com.justunfold.reposcoutapp.data.repository

import com.justunfold.reposcoutapp.core.database.dao.BookmarkDao
import com.justunfold.reposcoutapp.data.mapper.toDomain
import com.justunfold.reposcoutapp.data.mapper.toEntity
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookmarkRepositoryImpl(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override suspend fun bookmarkRepository(repository: RepositoryItem) {
        bookmarkDao.upsert(repository.toEntity())
    }

    override suspend fun removeBookmark(id: Long) {
        bookmarkDao.deleteById(id)
    }

    override fun getAllSavedRepositories(): Flow<List<RepositoryItem>> {
        return bookmarkDao.getAllSavedRepositories().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun isBookmarked(id: Long): Flow<Boolean> {
        return bookmarkDao.isRepositoryBookmarked(id)
    }

    override suspend fun getSavedRepository(owner: String, repo: String): RepositoryItem? {
        return bookmarkDao.getSavedRepositoryByName(owner, repo)?.toDomain()
    }
}
