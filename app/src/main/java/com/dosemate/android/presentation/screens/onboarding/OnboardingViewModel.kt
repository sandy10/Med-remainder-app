package com.dosemate.android.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosemate.android.data.datastore.AppPreferences
import com.dosemate.android.domain.model.UserProfile
import com.dosemate.android.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateRole(role: UserRole) {
        _uiState.update { it.copy(role = role) }
    }

    fun nextStep() {
        _uiState.update { 
            val next = it.currentStep + 1
            it.copy(currentStep = if (next <= 2) next else 2) 
        }
    }

    fun previousStep() {
        _uiState.update { 
            val prev = it.currentStep - 1
            it.copy(currentStep = if (prev >= 0) prev else 0) 
        }
    }

    /**
     * Completes onboarding and saves the profile to DataStore.
     * @param onComplete Callback invoked when saving is finished to trigger navigation.
     */
    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val state = _uiState.value
            val profile = UserProfile(
                name = state.name,
                role = state.role,
                onboardingComplete = true
            )
            appPreferences.saveUserProfile(profile)
            _uiState.update { it.copy(isSaving = false) }
            onComplete()
        }
    }
}
