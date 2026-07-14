package com.dosemate.android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dosemate.android.R
import com.dosemate.android.domain.model.DoseStatus
import com.dosemate.android.presentation.theme.SnoozeColor
import com.dosemate.android.presentation.theme.PendingColor

/**
 * Pill-shaped status badge that matches the Stitch Home Dashboard design.
 * - Taken   → green secondary container
 * - Upcoming → blue primary container
 * - Scheduled / Pending → gray surface variant
 * - Missed  → error container
 *
 * @param status     The [DoseStatus] to display.
 * @param isUpcoming True if this pending dose is the nearest upcoming dose.
 */
@Composable
fun StatusBadge(
    status: DoseStatus,
    modifier: Modifier = Modifier,
    isUpcoming: Boolean = false
) {
    // Map each status to (bgColor, textColor, label) matching Stitch pill style
    val (bgColor, textColor, label) = when {
        status == DoseStatus.TAKEN ->
            Triple(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer,
                stringResource(R.string.status_taken)
            )
        status == DoseStatus.MISSED ->
            Triple(
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer,
                stringResource(R.string.status_missed)
            )
        status == DoseStatus.SNOOZED ->
            Triple(
                SnoozeColor.copy(alpha = 0.15f),
                SnoozeColor,
                stringResource(R.string.status_snoozed)
            )
        status == DoseStatus.SKIPPED ->
            Triple(
                PendingColor.copy(alpha = 0.15f),
                PendingColor,
                stringResource(R.string.status_skipped)
            )
        status == DoseStatus.PENDING && isUpcoming ->
            Triple(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.onPrimaryContainer,
                stringResource(R.string.status_upcoming)
            )
        else ->
            Triple(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant,
                stringResource(R.string.status_scheduled)
            )
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = textColor,
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}
