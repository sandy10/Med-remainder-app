package com.dosemate.android.domain.repository

import com.dosemate.android.domain.model.DoseLog
import com.dosemate.android.domain.model.DoseStatus
import kotlinx.coroutines.flow.Flow

/**
 * Contract for dose log operations.
 * Powers the Home dashboard (today's doses) and the History screen.
 */
interface DoseLogRepository {

    /**
     * Returns all dose logs scheduled within the given epoch-millis range.
     * Use for both "today" (start/end of today) and "7/30-day history".
     */
    fun getDoseLogsForRange(startMillis: Long, endMillis: Long): Flow<List<DoseLog>>

    /** Returns all dose logs for a specific medication. */
    fun getDoseLogsForMedication(medicationId: Int): Flow<List<DoseLog>>


    /**
     * Inserts a new dose log entry (called when a reminder fires).
     * Returns the generated row ID.
     */
    suspend fun insertDoseLog(doseLog: DoseLog): Long

    /**
     * Updates the status and optional takenTime of an existing log entry.
     * Called when user taps "Mark Taken", "Snooze", or "Skip".
     */
    suspend fun updateDoseStatus(id: Int, status: DoseStatus, takenTime: Long? = null)

    /**
     * Deletes all dose logs for a given medication.
     * Called when a medication is deleted by the user.
     */
    suspend fun deleteLogsForMedication(medicationId: Int)
}
