package com.justunfold.reposcoutapp

import android.app.Application
import com.justunfold.reposcoutapp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RepoScoutApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RepoScoutApp)
            modules(appModules)
        }
    }
}
