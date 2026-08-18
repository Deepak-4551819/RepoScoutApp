package com.justunfold.reposcoutapp.domain.model

data class RepositoryItem(
    val id: Long,
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
    val isBookmarked: Boolean = false
)
