package com.dosemate.android.presentation.screens.settings

import com.dosemate.android.domain.model.UserProfile

/**
 * UI State for the Settings screen.
 */
data class SettingsUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDarkMode: Boolean = false,         // Persisted dark mode preference
    val version: String = "1.0.0" // TODO: Get from BuildConfig
)
