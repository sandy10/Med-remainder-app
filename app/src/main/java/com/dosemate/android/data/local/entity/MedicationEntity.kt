package com.dosemate.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dosemate.android.domain.model.MedicationFrequency
import com.dosemate.android.domain.model.MedicationType

/**
 * Room entity for the medications table.
 * Times are stored as a comma-separated string (e.g. "08:00,20:00")
 * and converted to/from List<String> via the TypeConverter.
 */
@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dosage: String,
    val type: MedicationType = MedicationType.TABLET,
    val frequency: MedicationFrequency = MedicationFrequency.ONCE_DAILY,
    val reminderTimes: String = "",  // comma-separated HH:mm values
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val notes: String = "",
    val isActive: Boolean = true
)
