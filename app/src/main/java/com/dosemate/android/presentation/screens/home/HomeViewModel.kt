package com.dosemate.android.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosemate.android.data.datastore.AppPreferences
import com.dosemate.android.domain.model.DoseStatus
import com.dosemate.android.domain.repository.DoseLogRepository
import com.dosemate.android.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val doseLogRepository: DoseLogRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeDashboardData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeDashboardData>> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Combine UserProfile (for name) and DoseLogs (for today's list)
            combine(
                appPreferences.userProfileFlow,
                doseLogRepository.getDoseLogsForRange(startOfDay, endOfDay)
            ) { profile, logs ->
                val taken = logs.count { it.status == DoseStatus.TAKEN }
                HomeDashboardData(
                    userName = profile.name,
                    doseLogs = logs,
                    takenCount = taken,
                    totalCount = logs.size
                )
            }
            .catch { e ->
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
            .collect { data ->
                if (data.doseLogs.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success(data)
                }
            }
        }
    }

    fun markDoseTaken(doseLogId: Int) {
        viewModelScope.launch {
            doseLogRepository.updateDoseStatus(doseLogId, DoseStatus.TAKEN, System.currentTimeMillis())
        }
    }
}
