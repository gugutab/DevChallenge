package com.tabdeveloper.devchallenge.application

import android.app.Application
import timber.log.Timber

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initializing Timber
        Timber.plant(Timber.DebugTree())
    }
}