package com.dosemate.android.presentation.screens.medicationdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dosemate.android.domain.repository.DoseLogRepository
import com.dosemate.android.domain.repository.MedicationRepository
import com.dosemate.android.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.dosemate.android.utils.ReminderScheduler

/**
 * ViewModel for [MedicationDetailScreen].
 * Fetches medication details and recent dose logs using type-safe navigation arguments.
 */
@HiltViewModel
class MedicationDetailViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val doseLogRepository: DoseLogRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Extracting medicationId using the new type-safe toRoute() extension
    private val detail: Screen.MedicationDetail = savedStateHandle.toRoute()
    private val medicationId: Int = detail.medicationId.toIntOrNull() ?: -1

    private val _uiState = MutableStateFlow(MedicationDetailUiState())
    val uiState: StateFlow<MedicationDetailUiState> = _uiState.asStateFlow()

    init {
        if (medicationId != -1) {
            loadMedicationDetails()
            loadRecentHistory()
        } else {
            _uiState.update { it.copy(error = "Invalid Medication ID") }
        }
    }

    private fun loadMedicationDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Observe the medication as a Flow so any update (e.g. after Edit)
            // is instantly reflected in the UI without leaving the screen.
            medicationRepository.getMedicationByIdFlow(medicationId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { medication ->
                    if (medication != null) {
                        _uiState.update { it.copy(isLoading = false, medication = medication) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Medication not found") }
                    }
                }
        }
    }

    private fun loadRecentHistory() {
        viewModelScope.launch {
            doseLogRepository.getDoseLogsForMedication(medicationId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { history ->
                    _uiState.update { it.copy(recentHistory = history.take(10)) }
                }
        }
    }

    fun deleteMedication(onDeleted: () -> Unit) {
        val med = _uiState.value.medication ?: return
        viewModelScope.launch {
            ReminderScheduler.cancelReminders(context, med)
            medicationRepository.deleteMedication(med)
            onDeleted()
        }
    }

    fun toggleDeleteDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteDialog = show) }
    }
}
