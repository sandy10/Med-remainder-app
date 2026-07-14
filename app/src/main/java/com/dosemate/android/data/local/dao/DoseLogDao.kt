package com.dosemate.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dosemate.android.data.local.entity.DoseLogEntity
import com.dosemate.android.domain.model.DoseStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO for dose log operations.
 * Supports the Home dashboard (today's doses) and History screen (range queries).
 */
@Dao
interface DoseLogDao {

    /**
     * Returns dose logs scheduled between [startMillis] and [endMillis].
     * Use start/end of today for the dashboard; a wider range for history.
     */
    @Query(
        "SELECT * FROM dose_logs " +
        "WHERE scheduledTime >= :startMillis AND scheduledTime <= :endMillis " +
        "ORDER BY scheduledTime ASC"
    )
    fun getDoseLogsForRange(startMillis: Long, endMillis: Long): Flow<List<DoseLogEntity>>

    /** Returns all dose logs for a specific medication. */
    @Query("SELECT * FROM dose_logs WHERE medicationId = :medicationId ORDER BY scheduledTime DESC")
    fun getDoseLogsForMedication(medicationId: Int): Flow<List<DoseLogEntity>>


    /**
     * Insert a dose log entry when a reminder fires.
     * Returns the generated row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoseLog(entity: DoseLogEntity): Long

    /**
     * Update the status and optional takenTime of an existing log.
     * Called when user marks taken, snoozes, or skips via notification or in-app.
     */
    @Query("UPDATE dose_logs SET status = :status, takenTime = :takenTime WHERE id = :id")
    suspend fun updateDoseStatus(id: Int, status: DoseStatus, takenTime: Long?)

    /** Delete all logs for a medication when the medication is deleted. */
    @Query("DELETE FROM dose_logs WHERE medicationId = :medicationId")
    suspend fun deleteLogsForMedication(medicationId: Int)
}
