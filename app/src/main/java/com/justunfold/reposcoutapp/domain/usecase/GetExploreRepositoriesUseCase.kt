package com.justunfold.reposcoutapp.domain.usecase

import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.GithubRepository

class GetExploreRepositoriesUseCase(
    private val githubRepository: GithubRepository
) {
    suspend operator fun invoke(page: Int = 1): ApiResult<List<RepositoryItem>> {
        return githubRepository.searchRepositories(query = "android", page = page)
    }
}
