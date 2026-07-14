package com.dosemate.android.domain.model

/**
 * Status of a single dose event.
 */
enum class DoseStatus {
    PENDING,  // Reminder fired but no action taken yet
    TAKEN,    // User confirmed the dose was taken
    MISSED,   // Grace window passed without action
    SNOOZED,  // User snoozed; will be reminded again
    SKIPPED   // User deliberately skipped this dose
}

/**
 * Domain model representing a single dose log entry.
 *
 * @param id              Unique identifier.
 * @param medicationId    FK linking to the parent Medication.
 * @param medicationName  Denormalised name for display without joins.
 * @param dosage          Denormalised dosage string for display.
 * @param scheduledTime   Epoch millis of the planned reminder time.
 * @param takenTime       Epoch millis when the dose was actually taken; null if not taken.
 * @param status          Current status of this dose event.
 */
data class DoseLog(
    val id: Int = 0,
    val medicationId: Int,
    val medicationName: String,
    val dosage: String,
    val scheduledTime: Long,
    val takenTime: Long? = null,
    val status: DoseStatus = DoseStatus.PENDING
)
