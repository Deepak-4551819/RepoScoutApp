package com.justunfold.reposcoutapp.di

import com.justunfold.reposcoutapp.features.bookmarks.BookmarksViewModel
import com.justunfold.reposcoutapp.features.detail.DetailViewModel
import com.justunfold.reposcoutapp.features.explore.ExploreViewModel
import com.justunfold.reposcoutapp.features.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ExploreViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { DetailViewModel(get(), get(), get()) }
    viewModel { BookmarksViewModel(get(), get()) }
}
