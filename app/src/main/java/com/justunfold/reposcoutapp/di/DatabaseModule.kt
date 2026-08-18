package com.justunfold.reposcoutapp.di

import androidx.room.Room
import com.justunfold.reposcoutapp.core.database.RepoScoutDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            RepoScoutDatabase::class.java,
            "reposcout.db"
        ).fallbackToDestructiveMigration(true).build()
    }
    single { get<RepoScoutDatabase>().bookmarkDao }
}
