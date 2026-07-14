package com.dosemate.android.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosemate.android.data.datastore.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [SettingsScreen].
 * Manages user preferences and app settings including dark mode toggle.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadDarkMode()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            appPreferences.userProfileFlow
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { profile ->
                    _uiState.update { it.copy(isLoading = false, userProfile = profile) }
                }
        }
    }

    /**
     * Collects the persisted dark mode flag from DataStore
     * and keeps [SettingsUiState.isDarkMode] in sync.
     */
    private fun loadDarkMode() {
        viewModelScope.launch {
            appPreferences.isDarkModeFlow.collect { enabled ->
                _uiState.update { it.copy(isDarkMode = enabled) }
            }
        }
    }

    fun updateName(newName: String) {
        val currentProfile = _uiState.value.userProfile ?: return
        viewModelScope.launch {
            appPreferences.saveUserProfile(currentProfile.copy(name = newName))
        }
    }

    /**
     * Flips the dark mode state and persists it to DataStore.
     * The new value flows back through [isDarkModeFlow] and updates the UI.
     */
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.saveDarkMode(enabled)
        }
    }
}
