package com.justunfold.reposcoutapp.di

import com.justunfold.reposcoutapp.core.network.KtorClientFactory
import org.koin.dsl.module

val networkModule = module {
    single { KtorClientFactory.create() }
}
