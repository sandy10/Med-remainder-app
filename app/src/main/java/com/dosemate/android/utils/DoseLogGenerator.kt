package com.dosemate.android.utils

import com.dosemate.android.domain.model.DoseLog
import com.dosemate.android.domain.model.DoseStatus
import com.dosemate.android.domain.model.Medication
import java.util.Calendar

object DoseLogGenerator {
    fun generateForToday(medications: List<Medication>, existingLogs: List<DoseLog>): List<DoseLog> {
        val calendar = Calendar.getInstance()
        val existingScheduledTimes = existingLogs.map { it.medicationId to it.scheduledTime }
        
        val newLogs = mutableListOf<DoseLog>()
        for (med in medications) {
            for (timeStr in med.reminderTimes) {
                val parts = timeStr.split(":")
                if (parts.size == 2) {
                    val hour = parts[0].toIntOrNull() ?: continue
                    val minute = parts[1].toIntOrNull() ?: continue
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val doseTime = calendar.timeInMillis
                    
                    if (!existingScheduledTimes.contains(med.id to doseTime)) {
                        newLogs.add(
                            DoseLog(
                                id = 0,
                                medicationId = med.id,
                                medicationName = med.name,
                                dosage = med.dosage,
                                scheduledTime = doseTime,
                                takenTime = null,
                                status = DoseStatus.PENDING
                            )
                        )
                    }
                }
            }
        }
        return newLogs
    }
}
