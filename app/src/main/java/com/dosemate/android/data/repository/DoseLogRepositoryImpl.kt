package com.dosemate.android.data.repository

import com.dosemate.android.data.local.dao.DoseLogDao
import com.dosemate.android.data.local.entity.DoseLogEntity
import com.dosemate.android.domain.model.DoseLog
import com.dosemate.android.domain.model.DoseStatus
import com.dosemate.android.domain.repository.DoseLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [DoseLogRepository].
 * Maps between Room [DoseLogEntity] and domain [DoseLog].
 */
class DoseLogRepositoryImpl @Inject constructor(
    private val dao: DoseLogDao
) : DoseLogRepository {

    override fun getDoseLogsForRange(startMillis: Long, endMillis: Long): Flow<List<DoseLog>> =
        dao.getDoseLogsForRange(startMillis, endMillis).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getDoseLogsForMedication(medicationId: Int): Flow<List<DoseLog>> =
        dao.getDoseLogsForMedication(medicationId).map { entities ->
            entities.map { it.toDomain() }
        }


    override suspend fun insertDoseLog(doseLog: DoseLog): Long =
        dao.insertDoseLog(doseLog.toEntity())

    override suspend fun updateDoseStatus(id: Int, status: DoseStatus, takenTime: Long?) =
        dao.updateDoseStatus(id, status, takenTime)

    override suspend fun deleteLogsForMedication(medicationId: Int) =
        dao.deleteLogsForMedication(medicationId)

    // ── Mapping helpers ──────────────────────────────────────────

    private fun DoseLogEntity.toDomain() = DoseLog(
        id = id,
        medicationId = medicationId,
        medicationName = medicationName,
        dosage = dosage,
        scheduledTime = scheduledTime,
        takenTime = takenTime,
        status = status
    )

    private fun DoseLog.toEntity() = DoseLogEntity(
        id = id,
        medicationId = medicationId,
        medicationName = medicationName,
        dosage = dosage,
        scheduledTime = scheduledTime,
        takenTime = takenTime,
        status = status
    )
}
