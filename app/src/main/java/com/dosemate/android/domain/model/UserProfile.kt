package com.dosemate.android.domain.model

/**
 * Role of the app user — affects greeting text and future caregiver alert features.
 */
enum class UserRole {
    PATIENT,
    CAREGIVER
}

/**
 * Domain model representing the local user profile stored in DataStore.
 *
 * @param name               User's display name entered during onboarding.
 * @param role               Whether the user is a patient or a caregiver.
 * @param onboardingComplete True after the user has completed all 3 onboarding steps.
 */
data class UserProfile(
    val name: String = "",
    val role: UserRole = UserRole.PATIENT,
    val onboardingComplete: Boolean = false
)
