package com.justunfold.reposcoutapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("incomplete_results") val incompleteResults: Boolean = false,
    @SerialName("items") val items: List<RepositoryDto> = emptyList()
)
