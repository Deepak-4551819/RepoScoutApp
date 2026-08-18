package com.justunfold.reposcoutapp.domain.repository

import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem

interface GithubRepository {
    suspend fun searchRepositories(query: String, page: Int, perPage: Int = 20): ApiResult<List<RepositoryItem>>
    suspend fun getRepositoryDetail(owner: String, repo: String): ApiResult<RepositoryItem>
}
