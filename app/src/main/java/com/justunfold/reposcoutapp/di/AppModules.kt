package com.justunfold.reposcoutapp.di

import com.justunfold.reposcoutapp.core.theme.ThemeManager
import org.koin.dsl.module

val themeModule = module {
    single { ThemeManager() }
}

val appModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
    themeModule
)
