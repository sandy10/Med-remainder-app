package com.dosemate.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dosemate.android.domain.model.DoseStatus

/**
 * Room entity for the dose_logs table.
 * Tracks every individual dose event fired by the reminder engine.
 */
@Entity(tableName = "dose_logs")
data class DoseLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicationId: Int,
    val medicationName: String,
    val dosage: String,
    val scheduledTime: Long,
    val takenTime: Long? = null,
    val status: DoseStatus = DoseStatus.PENDING
)
