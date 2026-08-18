package com.justunfold.reposcoutapp.domain.usecase

import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.GithubRepository

class SearchRepositoriesUseCase(
    private val githubRepository: GithubRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): ApiResult<List<RepositoryItem>> {
        if (query.isBlank()) return ApiResult.Success(emptyList())
        return githubRepository.searchRepositories(query = query.trim(), page = page)
    }
}
