package com.justunfold.reposcoutapp.di

import com.justunfold.reposcoutapp.domain.usecase.GetExploreRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.GetRepositoryDetailUseCase
import com.justunfold.reposcoutapp.domain.usecase.GetSavedRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.IsBookmarkedUseCase
import com.justunfold.reposcoutapp.domain.usecase.SearchRepositoriesUseCase
import com.justunfold.reposcoutapp.domain.usecase.ToggleBookmarkUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetExploreRepositoriesUseCase(get()) }
    factory { SearchRepositoriesUseCase(get()) }
    factory { GetRepositoryDetailUseCase(get(), get()) }
    factory { ToggleBookmarkUseCase(get()) }
    factory { GetSavedRepositoriesUseCase(get()) }
    factory { IsBookmarkedUseCase(get()) }
}
