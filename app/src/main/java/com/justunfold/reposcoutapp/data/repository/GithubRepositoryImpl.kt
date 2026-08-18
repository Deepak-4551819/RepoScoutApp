package com.justunfold.reposcoutapp.data.repository

import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.data.mapper.toDomain
import com.justunfold.reposcoutapp.data.remote.GithubRemoteDataSource
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.GithubRepository

class GithubRepositoryImpl(
    private val remoteDataSource: GithubRemoteDataSource
) : GithubRepository {

    override suspend fun searchRepositories(query: String, page: Int, perPage: Int): ApiResult<List<RepositoryItem>> {
        return when (val result = remoteDataSource.searchRepositories(query, page, perPage)) {
            is ApiResult.Success -> {
                ApiResult.Success(result.data.items.map { it.toDomain() })
            }
            is ApiResult.Error -> result
        }
    }

    override suspend fun getRepositoryDetail(owner: String, repo: String): ApiResult<RepositoryItem> {
        return when (val result = remoteDataSource.getRepositoryDetail(owner, repo)) {
            is ApiResult.Success -> {
                ApiResult.Success(result.data.toDomain())
            }
            is ApiResult.Error -> result
        }
    }
}
