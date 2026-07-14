package com.dosemate.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dosemate.android.data.datastore.AppPreferences
import com.dosemate.android.presentation.navigation.AppNavGraph
import com.dosemate.android.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle splash screen transition
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Edge-to-edge layout
        enableEdgeToEdge()

        setContent {
            // Observe the persisted dark mode preference from DataStore.
            // When the user toggles dark mode in Settings, this recomposes
            // AppTheme immediately — no restart needed.
            val isDarkMode by appPreferences.isDarkModeFlow.collectAsState(initial = false)

            AppTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(appPreferences = appPreferences)
                }
            }
        }
    }
}
