package com.justunfold.reposcoutapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_repositories")
data class SavedRepositoryEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val description: String?,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val openIssues: Int,
    val language: String?,
    val licenseName: String?,
    val htmlUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val savedAtEpochMillis: Long = System.currentTimeMillis()
)
