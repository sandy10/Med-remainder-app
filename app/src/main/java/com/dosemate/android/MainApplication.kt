package com.dosemate.android

import android.app.Application
import com.dosemate.android.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class.
 */
@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Timber for logging
        Timber.plant(Timber.DebugTree())
        
        // Create notification channel for medication reminders
        NotificationHelper.createNotificationChannel(this)
    }
}
