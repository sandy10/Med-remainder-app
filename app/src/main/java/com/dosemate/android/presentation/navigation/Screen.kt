package com.dosemate.android.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for the application.
 * Using Navigation 2.8.0+ Serializable objects.
 */
sealed interface Screen {
    
    @Serializable
    data object Splash : Screen
    
    @Serializable
    data object Onboarding : Screen
    
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object MedicationList : Screen
    
    @Serializable
    data object History : Screen
    
    @Serializable
    data object Settings : Screen
    
    @Serializable
    data object AddMedication : Screen
    
    @Serializable
    data class MedicationDetail(val medicationId: String) : Screen
    
    @Serializable
    data class EditMedication(val medicationId: String) : Screen
}
