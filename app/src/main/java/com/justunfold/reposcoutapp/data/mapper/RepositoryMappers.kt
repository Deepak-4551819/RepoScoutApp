package com.justunfold.reposcoutapp.data.mapper

import com.justunfold.reposcoutapp.core.database.entity.SavedRepositoryEntity
import com.justunfold.reposcoutapp.data.remote.dto.RepositoryDto
import com.justunfold.reposcoutapp.domain.model.RepositoryItem

fun RepositoryDto.toDomain(isBookmarked: Boolean = false): RepositoryItem {
    return RepositoryItem(
        id = id,
        name = name,
        ownerName = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        description = description ?: "No description provided.",
        stars = stargazersCount,
        forks = forksCount,
        watchers = watchersCount,
        openIssues = openIssuesCount,
        language = language ?: "Unknown",
        licenseName = license?.name ?: license?.spdxId ?: "No License",
        htmlUrl = htmlUrl,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        isBookmarked = isBookmarked
    )
}

fun SavedRepositoryEntity.toDomain(): RepositoryItem {
    return RepositoryItem(
        id = id,
        name = name,
        ownerName = ownerName,
        ownerAvatarUrl = ownerAvatarUrl,
        description = description,
        stars = stars,
        forks = forks,
        watchers = watchers,
        openIssues = openIssues,
        language = language,
        licenseName = licenseName,
        htmlUrl = htmlUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isBookmarked = true
    )
}

fun RepositoryItem.toEntity(): SavedRepositoryEntity {
    return SavedRepositoryEntity(
        id = id,
        name = name,
        ownerName = ownerName,
        ownerAvatarUrl = ownerAvatarUrl,
        description = description,
        stars = stars,
        forks = forks,
        watchers = watchers,
        openIssues = openIssues,
        language = language,
        licenseName = licenseName,
        htmlUrl = htmlUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
