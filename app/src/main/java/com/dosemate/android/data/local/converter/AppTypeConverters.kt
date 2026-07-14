package com.dosemate.android.data.local.converter

import androidx.room.TypeConverter
import com.dosemate.android.domain.model.DoseStatus
import com.dosemate.android.domain.model.MedicationFrequency
import com.dosemate.android.domain.model.MedicationType

/**
 * Room TypeConverters for enum fields and comma-separated time lists.
 * Registered in AppDatabase via @TypeConverters annotation.
 */
class AppTypeConverters {

    // ── MedicationType ────────────────────────────────────────────
    @TypeConverter
    fun fromMedicationType(value: MedicationType): String = value.name

    @TypeConverter
    fun toMedicationType(value: String): MedicationType =
        MedicationType.valueOf(value)

    // ── MedicationFrequency ───────────────────────────────────────
    @TypeConverter
    fun fromMedicationFrequency(value: MedicationFrequency): String = value.name

    @TypeConverter
    fun toMedicationFrequency(value: String): MedicationFrequency =
        MedicationFrequency.valueOf(value)

    // ── DoseStatus ────────────────────────────────────────────────
    @TypeConverter
    fun fromDoseStatus(value: DoseStatus): String = value.name

    @TypeConverter
    fun toDoseStatus(value: String): DoseStatus = DoseStatus.valueOf(value)
}
