package com.dosemate.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dosemate.android.data.local.converter.AppTypeConverters
import com.dosemate.android.data.local.dao.DoseLogDao
import com.dosemate.android.data.local.dao.MedicationDao
import com.dosemate.android.data.local.dao.PlaceholderDao
import com.dosemate.android.data.local.entity.DoseLogEntity
import com.dosemate.android.data.local.entity.MedicationEntity
import com.dosemate.android.data.local.entity.PlaceholderEntity

/**
 * Main Room database for MedDose.
 * Bump version and add a migration strategy when adding new entities or columns.
 */
@Database(
    entities = [
        PlaceholderEntity::class,
        MedicationEntity::class,
        DoseLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val placeholderDao: PlaceholderDao
    abstract val medicationDao: MedicationDao
    abstract val doseLogDao: DoseLogDao
}
