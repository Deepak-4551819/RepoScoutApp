package com.justunfold.reposcoutapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.justunfold.reposcoutapp.core.database.dao.BookmarkDao
import com.justunfold.reposcoutapp.core.database.entity.SavedRepositoryEntity

@Database(
    entities = [SavedRepositoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RepoScoutDatabase : RoomDatabase() {
    abstract val bookmarkDao: BookmarkDao
}
