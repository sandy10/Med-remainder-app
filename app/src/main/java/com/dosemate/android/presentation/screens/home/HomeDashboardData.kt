package com.dosemate.android.presentation.screens.home

import com.dosemate.android.domain.model.DoseLog

/**
 * Data payload for the Home Dashboard's Success state.
 *
 * @param userName   User's name from DataStore to display greeting.
 * @param doseLogs   List of today's dose logs.
 * @param takenCount Number of doses marked as TAKEN today.
 * @param totalCount Total number of doses scheduled for today.
 */
data class HomeDashboardData(
    val userName: String = "",
    val doseLogs: List<DoseLog> = emptyList(),
    val takenCount: Int = 0,
    val totalCount: Int = 0
) {
    /** Progress from 0.0 to 1.0 for the daily progress bar. */
    val progress: Float
        get() = if (totalCount == 0) 0f else takenCount.toFloat() / totalCount.toFloat()
}
