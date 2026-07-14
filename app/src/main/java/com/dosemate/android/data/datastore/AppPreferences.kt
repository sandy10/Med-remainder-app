package com.dosemate.android.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dosemate.android.domain.model.UserProfile
import com.dosemate.android.domain.model.UserRole
import com.dosemate.android.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** DataStore extension property — one instance per process. */
private val Context.dataStore by preferencesDataStore(name = Constants.PREFERENCES_NAME)

/**
 * DataStore-backed preferences for the user profile and app settings.
 * Persists name, role, onboarding completion, and dark mode across app restarts.
 * Injected by Hilt as a Singleton.
 */
@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        // Key for persisting the user's dark mode preference
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    /** Emits the current [UserProfile] and updates whenever any value changes. */
    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[KEY_USER_NAME] ?: "",
            role = UserRole.valueOf(prefs[KEY_USER_ROLE] ?: UserRole.PATIENT.name),
            onboardingComplete = prefs[KEY_ONBOARDING_COMPLETE] ?: false
        )
    }

    /**
     * Emits true when the user has enabled dark mode, false otherwise.
     * Defaults to false (light mode) on first launch.
     */
    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: false
    }

    /** Saves the completed user profile after the onboarding flow finishes. */
    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = profile.name
            prefs[KEY_USER_ROLE] = profile.role.name
            prefs[KEY_ONBOARDING_COMPLETE] = profile.onboardingComplete
        }
    }

    /**
     * Persists the dark mode preference so it survives app restarts.
     * Called by [SettingsViewModel.toggleDarkMode].
     */
    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = enabled
        }
    }
}
