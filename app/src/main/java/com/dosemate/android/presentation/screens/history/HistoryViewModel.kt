package com.dosemate.android.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosemate.android.domain.model.DoseStatus
import com.dosemate.android.domain.repository.DoseLogRepository
import com.dosemate.android.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val doseLogRepository: DoseLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HistoryDashboardData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HistoryDashboardData>> = _uiState.asStateFlow()

    init {
        loadHistory(7) // Default to 7 days
    }

    /** Loads history for the last X days. */
    fun loadHistory(days: Int) {
        val calendar = Calendar.getInstance()
        // End of today
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endMillis = calendar.timeInMillis
        
        // Start of (today - days)
        calendar.add(Calendar.DAY_OF_YEAR, -days + 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startMillis = calendar.timeInMillis

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            doseLogRepository.getDoseLogsForRange(startMillis, endMillis)
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Failed to load history")
                }
                .collect { logs ->
                    if (logs.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        // Group by start of day for section headers
                        val grouped = logs.groupBy { log ->
                            val cal = Calendar.getInstance().apply { timeInMillis = log.scheduledTime }
                            cal.set(Calendar.HOUR_OF_DAY, 0)
                            cal.set(Calendar.MINUTE, 0)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            cal.timeInMillis
                        }
                        
                        val total = logs.size
                        val taken = logs.count { it.status == DoseStatus.TAKEN }
                        val rate = if (total == 0) 0f else taken.toFloat() / total.toFloat()
                        
                        val dailyRates = grouped.mapValues { (_, dayLogs) ->
                            val dTotal = dayLogs.size
                            val dTaken = dayLogs.count { it.status == DoseStatus.TAKEN }
                            if (dTotal == 0) 0f else dTaken.toFloat() / dTotal.toFloat()
                        }
                        
                        _uiState.value = UiState.Success(
                            HistoryDashboardData(
                                logsByDay = grouped,
                                adherenceRate = rate,
                                dailyRates = dailyRates,
                                timeRange = "Last $days Days"
                            )
                        )

                    }
                }
        }
    }
}
