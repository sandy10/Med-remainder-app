package com.dosemate.android.presentation.screens.addmedication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosemate.android.domain.model.Medication
import com.dosemate.android.domain.model.MedicationFrequency
import com.dosemate.android.domain.model.MedicationType
import com.dosemate.android.domain.repository.DoseLogRepository
import com.dosemate.android.domain.repository.MedicationRepository
import com.dosemate.android.utils.DoseLogGenerator
import com.dosemate.android.utils.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.app.AlarmManager
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository,
    // Injected so we can immediately write today's dose logs to the DB
    // This is what makes the Home dashboard show medications right away
    private val doseLogRepository: DoseLogRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {


    private val _uiState = MutableStateFlow(AddMedicationUiState())
    val uiState: StateFlow<AddMedicationUiState> = _uiState.asStateFlow()

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
        if (!state.isStep2Valid) return

        // Check for exact alarm permission on Android 12+ (API 31)
        if (!ignorePermission && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                _uiState.update { it.copy(showAlarmPermissionDialog = true) }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, showAlarmPermissionDialog = false) }
            val medication = Medication(
                name = state.name,
                dosage = state.dosage,
                type = state.type,
                frequency = state.frequency,
                reminderTimes = state.times,
                notes = state.notes
            )
            val id = medicationRepository.addMedication(medication)

            // Attach the real DB-generated ID to the saved medication object
            val savedMedication = medication.copy(id = id.toInt())

            // Schedule alarms for the new medication
            ReminderScheduler.scheduleReminders(context, savedMedication)

            // ── Generate today's dose logs immediately ────────────────────────
            // Without this step, the Home dashboard queries dose_logs and finds
            // nothing, so it always shows the Empty state even after adding meds.
            // DoseLogGenerator calculates the scheduled epoch-millis for each
            // reminder time and returns DoseLog objects with PENDING status.
            val todayLogs = DoseLogGenerator.generateForToday(
                medications = listOf(savedMedication),
                existingLogs = emptyList() // brand new medication — no logs yet
            )
            // Insert each generated log so the Home screen can display them
            todayLogs.forEach { log ->
                doseLogRepository.insertDoseLog(log)
            }
            // ─────────────────────────────────────────────────────────────────

            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
