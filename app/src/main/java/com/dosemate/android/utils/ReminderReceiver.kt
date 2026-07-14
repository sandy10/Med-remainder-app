package com.dosemate.android.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dosemate.android.domain.repository.MedicationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Receiver that triggers when a medication reminder alarm goes off.
 * Extracts medication info and shows a high-priority notification.
 */
@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: MedicationRepository

    override fun onReceive(context: Context, intent: Intent) {
        val medId = intent.getIntExtra("med_id", 0)
        val medName = intent.getStringExtra("med_name") ?: "Medication"
        val dosage = intent.getStringExtra("med_dosage") ?: ""

        Timber.d("Reminder alarm received for $medName (ID: $medId)")

        // Show the notification
        NotificationHelper.showNotification(
            context = context,
            medicationId = medId,
            title = "Time for $medName",
            message = "Take $dosage as scheduled."
        )

        // Reschedule the next occurrence for this medication
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val medications = repository.getAllMedications().first()
                val medication = medications.find { it.id == medId }
                if (medication != null) {
                    ReminderScheduler.scheduleReminders(context, medication)
                    Timber.d("Successfully rescheduled next reminder for $medName")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to reschedule next reminder for $medName")
            }
        }
    }
}
