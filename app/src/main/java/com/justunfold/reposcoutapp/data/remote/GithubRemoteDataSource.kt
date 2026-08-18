package com.justunfold.reposcoutapp.data.remote

import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.data.remote.dto.RepositoryDto
import com.justunfold.reposcoutapp.data.remote.dto.SearchResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import java.io.IOException

class GithubRemoteDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://api.github.com"
    }

    suspend fun searchRepositories(query: String, page: Int = 1, perPage: Int = 20): ApiResult<SearchResponseDto> {
        return safeApiCall {
            httpClient.get("$BASE_URL/search/repositories") {
                parameter("q", query)
                parameter("page", page)
                parameter("per_page", perPage)
                parameter("sort", "stars")
            }
        }
    }

    suspend fun getRepositoryDetail(owner: String, repo: String): ApiResult<RepositoryDto> {
        return safeApiCall {
            httpClient.get("$BASE_URL/repos/$owner/$repo")
        }
    }

    private suspend inline fun <reified T> safeApiCall(block: () -> HttpResponse): ApiResult<T> {
        return try {
            val response = block()
            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(response.body<T>())
                }
                HttpStatusCode.Forbidden -> {
                    val rateLimitRemaining = response.headers["x-ratelimit-remaining"]?.toIntOrNull()
                    val rateLimitReset = response.headers["x-ratelimit-reset"]?.toLongOrNull()
                    ApiResult.Error(
                        message = "GitHub API rate limit exceeded (60 req/hr). Please wait before trying again.",
                        statusCode = 403,
                        isRateLimit = (rateLimitRemaining == 0),
                        resetTimeEpochSeconds = rateLimitReset
                    )
                }
                HttpStatusCode.NotFound -> {
                    ApiResult.Error(message = "Requested repository was not found.", statusCode = 404)
                }
                else -> {
                    ApiResult.Error(message = "Server error: ${response.status.value}", statusCode = response.status.value)
                }
            }
        } catch (e: IOException) {
            ApiResult.Error(message = "No internet connection or network timeout.")
        } catch (e: Exception) {
            ApiResult.Error(message = e.localizedMessage ?: "An unexpected error occurred.")
        }
    }
}
