package com.dosemate.android.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dosemate.android.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for all medication CRUD operations.
 * Returns Flow where possible so the UI reacts to data changes automatically.
 */
@Dao
interface MedicationDao {

    /** Observe all active and inactive medications; emits on every DB change. */
    @Query("SELECT * FROM medications ORDER BY name ASC")
    fun getAllMedications(): Flow<List<MedicationEntity>>

    /** Fetch a single medication by its primary key. */
    @Query("SELECT * FROM medications WHERE id = :id LIMIT 1")
    suspend fun getMedicationById(id: Int): MedicationEntity?

    /** Observe a single medication by ID; emits whenever the row changes. */
    @Query("SELECT * FROM medications WHERE id = :id LIMIT 1")
    fun getMedicationByIdFlow(id: Int): Flow<MedicationEntity?>

    /**
     * Insert a new medication. Returns the generated row ID.
     * Used by the AddMedicationViewModel to confirm success.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(entity: MedicationEntity): Long

    /** Update an existing medication record. */
    @Update
    suspend fun updateMedication(entity: MedicationEntity)

    /** Delete a medication record. */
    @Delete
    suspend fun deleteMedication(entity: MedicationEntity)
}
