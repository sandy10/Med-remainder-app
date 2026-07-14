package com.dosemate.android.presentation.screens.history

import com.dosemate.android.domain.model.DoseLog

/**
 * Data payload for the History Screen.
 * Groups dose logs by day (epoch millis rounded to start of day) for display.
 *
 * @param logsByDay     Map of day (start of day millis) to list of doses for that day.
 * @param adherenceRate Float from 0.0 to 1.0 representing overall adherence in the selected range.
 * @param timeRange     String indicating the current range (e.g. "Last 7 Days").
 */
data class HistoryDashboardData(
    val logsByDay: Map<Long, List<DoseLog>> = emptyMap(),
    val adherenceRate: Float = 0f,
    val dailyRates: Map<Long, Float> = emptyMap(), // Added for Chart
    val timeRange: String = "Last 7 Days"
)

