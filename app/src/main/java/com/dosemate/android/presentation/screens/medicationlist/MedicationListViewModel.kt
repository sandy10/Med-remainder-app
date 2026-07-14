package com.dosemate.android.presentation.screens.medicationlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosemate.android.domain.model.Medication
import com.dosemate.android.domain.repository.MedicationRepository
import com.dosemate.android.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationListViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Medication>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Medication>>> = _uiState.asStateFlow()

    init {
        loadMedications()
    }

    private fun loadMedications() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            medicationRepository.getAllMedications()
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Failed to load medications")
                }
                .collect { list ->
                    if (list.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(list)
                    }
                }
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            medicationRepository.deleteMedication(medication)
            // The flow will automatically emit the new list, updating the UI.
        }
    }
}
