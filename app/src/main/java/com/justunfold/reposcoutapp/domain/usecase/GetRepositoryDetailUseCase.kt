package com.justunfold.reposcoutapp.domain.usecase

import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.BookmarkRepository
import com.justunfold.reposcoutapp.domain.repository.GithubRepository

class GetRepositoryDetailUseCase(
    private val githubRepository: GithubRepository,
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(owner: String, repo: String): ApiResult<RepositoryItem> {
        return when (val remoteResult = githubRepository.getRepositoryDetail(owner = owner, repo = repo)) {
            is ApiResult.Success -> remoteResult
            is ApiResult.Error -> {
                // If API call fails (offline, rate limit, timeout), check local bookmarks
                val localSavedRepo = bookmarkRepository.getSavedRepository(owner = owner, repo = repo)
                if (localSavedRepo != null) {
                    ApiResult.Success(localSavedRepo)
                } else {
                    remoteResult
                }
            }
        }
    }
}
