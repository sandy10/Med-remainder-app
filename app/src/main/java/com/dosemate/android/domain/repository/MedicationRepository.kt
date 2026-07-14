package com.dosemate.android.domain.repository

import com.dosemate.android.domain.model.Medication
import kotlinx.coroutines.flow.Flow

/**
 * Contract for all medication CRUD operations.
 * ViewModels depend on this interface; the data layer provides the implementation.
 */
interface MedicationRepository {

    /** Returns a live Flow of all medications for the current user. */
    fun getAllMedications(): Flow<List<Medication>>

    /** Returns a single medication by its ID, or null if not found. */
    suspend fun getMedicationById(id: Int): Medication?

    /** Returns a live Flow of a single medication; emits on every update. */
    fun getMedicationByIdFlow(id: Int): Flow<Medication?>

    /**
     * Inserts a new medication and returns the generated row ID.
     * Used by AddMedicationViewModel after form validation.
     */
    suspend fun addMedication(medication: Medication): Long

    /** Updates an existing medication record. */
    suspend fun updateMedication(medication: Medication)

    /** Deletes a medication and all its associated dose logs. */
    suspend fun deleteMedication(medication: Medication)
}
