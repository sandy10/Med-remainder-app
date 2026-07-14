package com.dosemate.android.data.repository

import com.dosemate.android.data.local.dao.MedicationDao
import com.dosemate.android.data.local.entity.MedicationEntity
import com.dosemate.android.domain.model.Medication
import com.dosemate.android.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [MedicationRepository].
 * Maps between the Room [MedicationEntity] and the domain [Medication] model.
 * Injected by Hilt via [RepositoryModule].
 */
class MedicationRepositoryImpl @Inject constructor(
    private val dao: MedicationDao
) : MedicationRepository {

    override fun getAllMedications(): Flow<List<Medication>> =
        dao.getAllMedications().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getMedicationById(id: Int): Medication? =
        dao.getMedicationById(id)?.toDomain()

    override fun getMedicationByIdFlow(id: Int): Flow<Medication?> =
        dao.getMedicationByIdFlow(id).map { it?.toDomain() }

    override suspend fun addMedication(medication: Medication): Long =
        dao.insertMedication(medication.toEntity())

    override suspend fun updateMedication(medication: Medication) =
        dao.updateMedication(medication.toEntity())

    override suspend fun deleteMedication(medication: Medication) =
        dao.deleteMedication(medication.toEntity())

    // ── Mapping helpers ──────────────────────────────────────────

    private fun MedicationEntity.toDomain() = Medication(
        id = id,
        name = name,
        dosage = dosage,
        type = type,
        frequency = frequency,
        reminderTimes = if (reminderTimes.isBlank()) emptyList()
                        else reminderTimes.split(","),
        startDate = startDate,
        endDate = endDate,
        notes = notes,
        isActive = isActive
    )

    private fun Medication.toEntity() = MedicationEntity(
        id = id,
        name = name,
        dosage = dosage,
        type = type,
        frequency = frequency,
        reminderTimes = reminderTimes.joinToString(","),
        startDate = startDate,
        endDate = endDate,
        notes = notes,
        isActive = isActive
    )
}
