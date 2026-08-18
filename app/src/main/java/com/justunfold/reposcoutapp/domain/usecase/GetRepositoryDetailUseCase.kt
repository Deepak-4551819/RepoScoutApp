package com.justunfold.reposcoutapp.domain.usecase

import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.GithubRepository

class GetRepositoryDetailUseCase(
    private val githubRepository: GithubRepository
) {
    suspend operator fun invoke(owner: String, repo: String): ApiResult<RepositoryItem> {
        return githubRepository.getRepositoryDetail(owner = owner, repo = repo)
    }
}
