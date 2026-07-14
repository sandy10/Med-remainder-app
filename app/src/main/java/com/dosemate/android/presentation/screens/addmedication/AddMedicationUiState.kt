package com.dosemate.android.presentation.screens.addmedication

import com.dosemate.android.domain.model.MedicationFrequency
import com.dosemate.android.domain.model.MedicationType

/**
 * UI state for the Add Medication flow (2 steps).
 *
 * @param name       Medication name.
 * @param dosage     Dosage string (e.g., "500mg").
 * @param type       Selected form factor (Tablet, Liquid, etc.).
 * @param frequency  How often to take it per day.
 * @param times      List of time strings (e.g., "08:00", "20:00") matching the frequency count.
 * @param notes      Optional usage instructions.
 * @param currentStep 1 for details, 2 for time picker.
 * @param isSaving   True while the repository is inserting the record.
 */
data class AddMedicationUiState(
    val name: String = "",
    val dosage: String = "",
    val type: MedicationType = MedicationType.TABLET,
    val frequency: MedicationFrequency = MedicationFrequency.ONCE_DAILY,
    val times: List<String> = listOf(""), // Default to one empty time for ONCE_DAILY

    val notes: String = "",
    val currentStep: Int = 1,
    val isSaving: Boolean = false,
    val showAlarmPermissionDialog: Boolean = false
) {
    /** Validates Step 1 so the "Next" button can be enabled. */
    val isStep1Valid: Boolean
        get() = name.isNotBlank() && dosage.isNotBlank()
        
    /** Validates Step 2 so the "Save" button can be enabled. */
    val isStep2Valid: Boolean
        get() = times.isNotEmpty() && !times.contains("")
}
