package com.dosemate.android.presentation.screens.onboarding

import com.dosemate.android.domain.model.UserRole

/**
 * UI State for the Onboarding flow.
 *
 * @param name          User's name input (Step 1).
 * @param role          Selected user role (Step 2).
 * @param currentStep   Which step of onboarding to show (0 = Name, 1 = Role, 2 = Permission).
 * @param isSaving      True when saving to DataStore at the end.
 */
data class OnboardingUiState(
    val name: String = "",
    val role: UserRole = UserRole.PATIENT,
    val currentStep: Int = 0,
    val isSaving: Boolean = false
)
