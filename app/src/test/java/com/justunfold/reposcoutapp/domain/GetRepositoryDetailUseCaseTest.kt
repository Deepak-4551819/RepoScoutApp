package com.justunfold.reposcoutapp.domain

import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.repository.BookmarkRepository
import com.justunfold.reposcoutapp.domain.repository.GithubRepository
import com.justunfold.reposcoutapp.domain.usecase.GetRepositoryDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRepositoryDetailUseCaseTest {

    private val githubRepository: GithubRepository = mockk()
    private val bookmarkRepository: BookmarkRepository = mockk()
    private lateinit var useCase: GetRepositoryDetailUseCase

    private val mockRepo = RepositoryItem(
        id = 101L,
        name = "retrofit",
        ownerName = "square",
        ownerAvatarUrl = "https://example.com/avatar.png",
        description = "Type-safe HTTP client",
        stars = 42000,
        forks = 7000,
        watchers = 500,
        openIssues = 120,
        language = "Java",
        licenseName = "Apache-2.0",
        htmlUrl = "https://github.com/square/retrofit",
        createdAt = "2013-05-13T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z",
        isBookmarked = true
    )

    @Before
    fun setUp() {
        useCase = GetRepositoryDetailUseCase(githubRepository, bookmarkRepository)
    }

    @Test
    fun `when remote API succeeds, returns remote repository data`() = runTest {
        coEvery { githubRepository.getRepositoryDetail("square", "retrofit") } returns ApiResult.Success(mockRepo)

        val result = useCase("square", "retrofit")

        assertTrue(result is ApiResult.Success)
        assertEquals(mockRepo, (result as ApiResult.Success).data)
    }

    @Test
    fun `when remote API fails but repository is saved offline, returns cached data`() = runTest {
        coEvery { githubRepository.getRepositoryDetail("square", "retrofit") } returns ApiResult.Error("No network connection")
        coEvery { bookmarkRepository.getSavedRepository("square", "retrofit") } returns mockRepo

        val result = useCase("square", "retrofit")

        assertTrue(result is ApiResult.Success)
        assertEquals("retrofit", (result as ApiResult.Success).data.name)
        assertTrue(result.data.isBookmarked)
    }

    @Test
    fun `when remote API fails and repository is not saved, returns API error`() = runTest {
        coEvery { githubRepository.getRepositoryDetail("unknown", "repo") } returns ApiResult.Error("Not Found", statusCode = 404)
        coEvery { bookmarkRepository.getSavedRepository("unknown", "repo") } returns null

        val result = useCase("unknown", "repo")

        assertTrue(result is ApiResult.Error)
        assertEquals("Not Found", (result as ApiResult.Error).message)
    }
}
