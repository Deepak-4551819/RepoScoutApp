package com.justunfold.reposcoutapp.features.explore

import app.cash.turbine.test
import com.justunfold.reposcoutapp.core.MainDispatcherRule
import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.usecase.GetExploreRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.GetSavedRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.ToggleBookmarkUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getExploreRepositoriesUseCase: GetExploreRepositoriesUseCase = mockk()
    private val getSavedRepositoriesUseCase: GetSavedRepositoriesUseCase = mockk()
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase = mockk(relaxed = true)

    private val sampleRepo = RepositoryItem(
        id = 1L,
        name = "compose-multiplatform",
        ownerName = "JetBrains",
        ownerAvatarUrl = "",
        description = "Compose Multiplatform UI",
        stars = 15000,
        forks = 1000,
        watchers = 200,
        openIssues = 50,
        language = "Kotlin",
        licenseName = "Apache-2.0",
        htmlUrl = "",
        createdAt = "",
        updatedAt = ""
    )

    @Test
    fun `initial load emits success state with repositories`() = runTest {
        every { getSavedRepositoriesUseCase() } returns flowOf(emptyList())
        coEvery { getExploreRepositoriesUseCase(page = 1) } returns ApiResult.Success(listOf(sampleRepo))

        val viewModel = ExploreViewModel(
            getExploreRepositoriesUseCase,
            getSavedRepositoriesUseCase,
            toggleBookmarkUseCase
        )

        viewModel.uiState.test {
            // Wait for all emissions triggered in init
            advanceUntilIdle()

            val successState = expectMostRecentItem()
            assertEquals(1, successState.repositories.size)
            assertEquals("compose-multiplatform", successState.repositories.first().name)
            assertFalse(successState.isLoading)
        }
    }

    @Test
    fun `when API returns rate limit error, uiState reflects rate limit message`() = runTest {
        every { getSavedRepositoriesUseCase() } returns flowOf(emptyList())
        coEvery { getExploreRepositoriesUseCase(page = 1) } returns ApiResult.Error(
            message = "Rate limit exceeded",
            statusCode = 403,
            isRateLimit = true
        )

        val viewModel = ExploreViewModel(
            getExploreRepositoriesUseCase,
            getSavedRepositoriesUseCase,
            toggleBookmarkUseCase
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isRateLimit)
        assertEquals("Rate limit exceeded", state.errorMessage)
    }

    @Test
    fun `ToggleBookmark intent triggers ToggleBookmarkUseCase`() = runTest {
        every { getSavedRepositoriesUseCase() } returns flowOf(emptyList())
        coEvery { getExploreRepositoriesUseCase(page = 1) } returns ApiResult.Success(listOf(sampleRepo))

        val viewModel = ExploreViewModel(
            getExploreRepositoriesUseCase,
            getSavedRepositoriesUseCase,
            toggleBookmarkUseCase
        )
        advanceUntilIdle()

        viewModel.onIntent(ExploreIntent.ToggleBookmark(sampleRepo))
        advanceUntilIdle()

        coVerify { toggleBookmarkUseCase(sampleRepo, false) }
    }
}
