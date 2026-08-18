package com.justunfold.reposcoutapp.features.search

import app.cash.turbine.test
import com.justunfold.reposcoutapp.core.MainDispatcherRule
import com.justunfold.reposcoutapp.core.network.ApiResult
import com.justunfold.reposcoutapp.domain.model.RepositoryItem
import com.justunfold.reposcoutapp.domain.usecase.GetSavedRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.SearchRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.ToggleBookmarkUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchRepositoriesUseCase: SearchRepositoriesUseCase = mockk()
    private val getSavedRepositoriesUseCase: GetSavedRepositoriesUseCase = mockk()
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase = mockk(relaxed = true)

    private val mockRepo = RepositoryItem(
        id = 99L,
        name = "ktor",
        ownerName = "ktorio",
        ownerAvatarUrl = "",
        description = "Connected applications framework",
        stars = 13000,
        forks = 1100,
        watchers = 300,
        openIssues = 80,
        language = "Kotlin",
        licenseName = "Apache-2.0",
        htmlUrl = "",
        createdAt = "",
        updatedAt = ""
    )

    @Test
    fun `search query triggers debounce and emits search results`() = runTest {
        every { getSavedRepositoriesUseCase() } returns flowOf(emptyList())
        coEvery { searchRepositoriesUseCase(query = "ktor", page = 1) } returns ApiResult.Success(listOf(mockRepo))

        val viewModel = SearchViewModel(
            searchRepositoriesUseCase,
            getSavedRepositoriesUseCase,
            toggleBookmarkUseCase
        )

        viewModel.uiState.test {
            val initial = awaitItem()
            assertTrue(initial.repositories.isEmpty())

            viewModel.onIntent(SearchIntent.QueryChanged("ktor"))
            
            // Advance past 400ms debounce threshold
            advanceTimeBy(450L)
            advanceUntilIdle()

            // Skip query updates to the final state with loaded repositories
            val finalState = expectMostRecentItem()
            assertEquals("ktor", finalState.query)
            assertEquals(1, finalState.repositories.size)
            assertEquals("ktorio", finalState.repositories.first().ownerName)
        }
    }

    @Test
    fun `ClearQuery intent resets query and empties list immediately`() = runTest {
        every { getSavedRepositoriesUseCase() } returns flowOf(emptyList())
        coEvery { searchRepositoriesUseCase(any(), any()) } returns ApiResult.Success(listOf(mockRepo))

        val viewModel = SearchViewModel(
            searchRepositoriesUseCase,
            getSavedRepositoriesUseCase,
            toggleBookmarkUseCase
        )

        viewModel.onIntent(SearchIntent.QueryChanged("ktor"))
        advanceTimeBy(450L)
        advanceUntilIdle()

        viewModel.onIntent(SearchIntent.ClearQuery)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.repositories.isEmpty())
    }
}
