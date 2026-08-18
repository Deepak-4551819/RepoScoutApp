package com.justunfold.reposcoutapp.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.justunfold.reposcoutapp.core.database.entity.SavedRepositoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Upsert
    suspend fun upsert(repository: SavedRepositoryEntity)

    @Query("DELETE FROM saved_repositories WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM saved_repositories ORDER BY savedAtEpochMillis DESC")
    fun getAllSavedRepositories(): Flow<List<SavedRepositoryEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_repositories WHERE id = :id)")
    fun isRepositoryBookmarked(id: Long): Flow<Boolean>

    @Query("SELECT * FROM saved_repositories WHERE id = :id LIMIT 1")
    suspend fun getSavedRepositoryById(id: Long): SavedRepositoryEntity?

    // Lookup saved repository by owner and name for offline details
    @Query("SELECT * FROM saved_repositories WHERE ownerName = :ownerName AND name = :repoName COLLATE NOCASE LIMIT 1")
    suspend fun getSavedRepositoryByName(ownerName: String, repoName: String): SavedRepositoryEntity?
}
