package com.justunfold.reposcoutapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("owner") val owner: OwnerDto,
    @SerialName("description") val description: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Int = 0,
    @SerialName("forks_count") val forksCount: Int = 0,
    @SerialName("watchers_count") val watchersCount: Int = 0,
    @SerialName("open_issues_count") val openIssuesCount: Int = 0,
    @SerialName("language") val language: String? = null,
    @SerialName("license") val license: LicenseDto? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class OwnerDto(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)

@Serializable
data class LicenseDto(
    @SerialName("key") val key: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("spdx_id") val spdxId: String? = null
)
