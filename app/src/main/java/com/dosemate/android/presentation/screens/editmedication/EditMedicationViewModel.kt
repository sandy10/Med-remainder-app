package com.dosemate.android.presentation.screens.editmedication

import android.app.AlarmManager
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dosemate.android.domain.model.Medication
import com.dosemate.android.domain.model.MedicationFrequency
import com.dosemate.android.domain.model.MedicationType
import com.dosemate.android.domain.repository.DoseLogRepository
import com.dosemate.android.domain.repository.MedicationRepository
import com.dosemate.android.presentation.navigation.Screen
import com.dosemate.android.presentation.screens.addmedication.AddMedicationUiState
import com.dosemate.android.utils.DoseLogGenerator
import com.dosemate.android.utils.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMedicationViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val doseLogRepository: DoseLogRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val medicationId: Int = savedStateHandle.toRoute<Screen.EditMedication>().medicationId.toIntOrNull() ?: -1

    private val _uiState = MutableStateFlow(AddMedicationUiState(isSaving = true))
    val uiState: StateFlow<AddMedicationUiState> = _uiState.asStateFlow()

    private var existingMedication: Medication? = null

    init {
        loadMedication()
    }

    private fun loadMedication() {
        if (medicationId == -1) return
        viewModelScope.launch {
            existingMedication = medicationRepository.getMedicationById(medicationId)
            existingMedication?.let { med ->
                _uiState.update {
                    it.copy(
                        name = med.name,
                        dosage = med.dosage,
                        type = med.type,
                        frequency = med.frequency,
                        times = med.reminderTimes,
                        notes = med.notes,
                        isSaving = false
                    )
                }
            }
        }
    }

    fun updateName(name: String) = _uiState.update { it.copy(name = name) }
    
    fun updateDosage(dosage: String) = _uiState.update { it.copy(dosage = dosage) }
    
    fun updateNotes(notes: String) = _uiState.update { it.copy(notes = notes) }
    
    fun updateType(type: MedicationType) = _uiState.update { it.copy(type = type) }
    
    fun updateFrequency(frequency: MedicationFrequency) {
        val size = when (frequency) {
            MedicationFrequency.ONCE_DAILY -> 1
            MedicationFrequency.TWICE_DAILY -> 2
            MedicationFrequency.THRICE_DAILY -> 3
            MedicationFrequency.CUSTOM -> 1 // Default to 1 for custom to start
        }
        val defaultTimes = List(size) { "" }
        _uiState.update { it.copy(frequency = frequency, times = defaultTimes) }
    }

    fun updateTime(index: Int, time: String) {
        _uiState.update { state ->
            val newTimes = state.times.toMutableList()
            if (index in newTimes.indices) {
                newTimes[index] = time
            }
            state.copy(times = newTimes)
        }
    }

    fun nextStep() = _uiState.update { it.copy(currentStep = 2) }
    
    fun previousStep() = _uiState.update { it.copy(currentStep = 1) }

    fun dismissPermissionDialog() = _uiState.update { it.copy(showAlarmPermissionDialog = false) }

    fun saveMedication(onSuccess: () -> Unit, ignorePermission: Boolean = false) {
        val state = _uiState.value
        val oldMed = existingMedication ?: return
        if (!state.isStep2Valid) return

        if (!ignorePermission && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                _uiState.update { it.copy(showAlarmPermissionDialog = true) }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, showAlarmPermissionDialog = false) }
            
            // Cancel old alarms so stale notifications don't fire
            ReminderScheduler.cancelReminders(context, oldMed)

            val updatedMedication = oldMed.copy(
                name = state.name,
                dosage = state.dosage,
                type = state.type,
                frequency = state.frequency,
                reminderTimes = state.times,
                notes = state.notes
            )
            medicationRepository.updateMedication(updatedMedication)

            // Schedule new alarms with the updated times
            ReminderScheduler.scheduleReminders(context, updatedMedication)

            // ── Refresh today's dose logs ─────────────────────────────────────
            // The Home screen and History screen read from the dose_logs table.
            // After an edit the old logs still carry the old scheduledTime, so
            // we delete all of today's pending logs for this medication and
            // regenerate fresh ones using the new reminder times.
            doseLogRepository.deleteLogsForMedication(updatedMedication.id)
            val freshLogs = DoseLogGenerator.generateForToday(
                medications = listOf(updatedMedication),
                existingLogs = emptyList()
            )
            freshLogs.forEach { log -> doseLogRepository.insertDoseLog(log) }
            // ─────────────────────────────────────────────────────────────────

            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
