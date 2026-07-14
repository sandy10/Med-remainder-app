package com.dosemate.android.domain.model

/**
 * Medication type — used for selecting the correct icon on cards.
 */
enum class MedicationType {
    TABLET,
    LIQUID,
    INJECTION,
    OTHER
}

/**
 * How often a medication should be taken per day.
 */
enum class MedicationFrequency {
    ONCE_DAILY,
    TWICE_DAILY,
    THRICE_DAILY,
    CUSTOM
}

/**
 * Domain model representing a single medication tracked by the user.
 *
 * @param id            Unique identifier (Room primary key).
 * @param name          Medication name, e.g. "Metformin".
 * @param dosage        Dosage description, e.g. "500mg".
 * @param type          Tablet, liquid, injection, or other.
 * @param frequency     How many times per day.
 * @param reminderTimes List of HH:mm strings for each reminder, e.g. ["08:00", "20:00"].
 * @param startDate     Epoch millis for the start date.
 * @param endDate       Epoch millis for the end date; null means ongoing.
 * @param notes         Optional usage notes, e.g. "Take with food".
 * @param isActive      Whether reminders are currently enabled for this medication.
 */
data class Medication(
    val id: Int = 0,
    val name: String,
    val dosage: String,
    val type: MedicationType = MedicationType.TABLET,
    val frequency: MedicationFrequency = MedicationFrequency.ONCE_DAILY,
    val reminderTimes: List<String> = emptyList(),
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val notes: String = "",
    val isActive: Boolean = true
)
