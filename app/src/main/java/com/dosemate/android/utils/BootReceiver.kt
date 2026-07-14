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
 * Reschedules all medication reminders when the device is rebooted.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: MedicationRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Timber.d("Boot completed, rescheduling all medication reminders")
            
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val medications = repository.getAllMedications().first()
                    medications.forEach { medication ->
                        ReminderScheduler.scheduleReminders(context, medication)
                    }
                    Timber.d("Rescheduled reminders for ${medications.size} medications")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to reschedule reminders on boot")
                }
            }
        }
    }
}
