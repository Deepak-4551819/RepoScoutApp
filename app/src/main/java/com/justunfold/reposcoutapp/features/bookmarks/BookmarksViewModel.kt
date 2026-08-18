package com.justunfold.reposcoutapp.features.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justunfold.reposcoutapp.domain.usecase.GetSavedRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val getSavedRepositoriesUseCase: GetSavedRepositoriesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    init {
        observeSavedRepositories()
    }

    fun onIntent(intent: BookmarksIntent) {
        when (intent) {
            is BookmarksIntent.RemoveBookmark -> removeBookmark(intent.id)
        }
    }

    private fun observeSavedRepositories() {
        getSavedRepositoriesUseCase()
            .onEach { savedList ->
                _uiState.update {
                    it.copy(
                        savedRepositories = savedList,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun removeBookmark(repoId: Long) {
        val targetItem = _uiState.value.savedRepositories.find { it.id == repoId } ?: return
        viewModelScope.launch {
            toggleBookmarkUseCase(targetItem, currentlyBookmarked = true)
        }
    }
}
