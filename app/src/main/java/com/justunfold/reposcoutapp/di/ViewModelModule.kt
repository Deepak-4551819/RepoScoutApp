package com.justunfold.reposcoutapp.di

import com.justunfold.reposcoutapp.features.explore.ExploreViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ExploreViewModel(get(), get(), get()) }
}
