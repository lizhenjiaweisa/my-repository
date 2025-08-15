package com.github.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global configurations
    }
}