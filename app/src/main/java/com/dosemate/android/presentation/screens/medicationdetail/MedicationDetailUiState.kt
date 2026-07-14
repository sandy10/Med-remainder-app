package com.dosemate.android.presentation.screens.medicationdetail

import com.dosemate.android.domain.model.DoseLog
import com.dosemate.android.domain.model.Medication

/**
 * UI State for the Medication Detail screen.
 */
data class MedicationDetailUiState(
    val medication: Medication? = null,
    val recentHistory: List<DoseLog> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false
)
