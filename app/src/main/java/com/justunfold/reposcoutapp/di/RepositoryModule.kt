package com.justunfold.reposcoutapp.di

import com.justunfold.reposcoutapp.data.remote.GithubRemoteDataSource
import com.justunfold.reposcoutapp.data.repository.BookmarkRepositoryImpl
import com.justunfold.reposcoutapp.data.repository.GithubRepositoryImpl
import com.justunfold.reposcoutapp.domain.repository.BookmarkRepository
import com.justunfold.reposcoutapp.domain.repository.GithubRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { GithubRemoteDataSource(get()) }
    single<GithubRepository> { GithubRepositoryImpl(get()) }
    single<BookmarkRepository> { BookmarkRepositoryImpl(get()) }
}
