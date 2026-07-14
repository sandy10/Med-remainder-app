package com.dosemate.android.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dosemate.android.domain.model.DoseLog
import com.dosemate.android.domain.model.DoseStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Card displaying a single dose entry on the Home Dashboard.
 * Shows medication name, dosage, scheduled time, and a [StatusBadge].
 *
 * @param doseLog     The dose log entry to display.
 * @param onMarkTaken Called when user taps the card/action to record taking the dose.
 * @param isUpcoming  True if this is the next upcoming pending dose.
 */
@Composable
fun DoseCard(
    doseLog: DoseLog,
    onMarkTaken: () -> Unit,
    modifier: Modifier = Modifier,
    isUpcoming: Boolean = false
) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val scheduledTimeStr = timeFormat.format(Date(doseLog.scheduledTime))

    val isLiquid = doseLog.medicationName.contains("syrup", ignoreCase = true) ||
                   doseLog.medicationName.contains("liquid", ignoreCase = true) ||
                   doseLog.medicationName.contains("drops", ignoreCase = true) ||
                   doseLog.medicationName.contains("elixir", ignoreCase = true) ||
                   doseLog.medicationName.contains("suspension", ignoreCase = true) ||
                   doseLog.dosage.contains("ml", ignoreCase = true) ||
                   doseLog.dosage.contains("drop", ignoreCase = true)

    val isInjection = doseLog.medicationName.contains("injection", ignoreCase = true) ||
                      doseLog.medicationName.contains("syringe", ignoreCase = true) ||
                      doseLog.medicationName.contains("vial", ignoreCase = true) ||
                      doseLog.dosage.contains("mcg", ignoreCase = true)

    val iconBgColor = if (doseLog.status == DoseStatus.TAKEN) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    val iconColor = if (doseLog.status == DoseStatus.TAKEN) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (doseLog.status != DoseStatus.TAKEN) {
                    Modifier.clickable { onMarkTaken() }
                } else Modifier
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 56dp icon container — matches Stitch spec
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = iconBgColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLiquid -> DropletIcon(color = iconColor, modifier = Modifier.fillMaxSize())
                    isInjection -> InjectionIcon(color = iconColor, modifier = Modifier.fillMaxSize())
                    else -> CapsuleIcon(color = iconColor, modifier = Modifier.fillMaxSize())
                }
            }

            // Medication name + dosage • time
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doseLog.medicationName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${doseLog.dosage} • $scheduledTimeStr",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status badge (pill-shaped, right side)
            StatusBadge(status = doseLog.status, isUpcoming = isUpcoming)
        }
    }
}

@Composable
fun CapsuleIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        rotate(45f) {
            val pillW = w * 0.35f
            val pillH = h * 0.85f
            val left = (w - pillW) / 2
            val top = (h - pillH) / 2
            
            drawRoundRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(pillW, pillH),
                cornerRadius = CornerRadius(pillW / 2, pillW / 2)
            )
            drawLine(
                color = Color.White,
                start = Offset(left, top + pillH / 2),
                end = Offset(left + pillW, top + pillH / 2),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun DropletIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w / 2, h * 0.15f)
            quadraticTo(w * 0.8f, h * 0.55f, w * 0.8f, h * 0.72f)
            arcTo(
                rect = Rect(w * 0.2f, h * 0.44f, w * 0.8f, h * 0.9f),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            quadraticTo(w * 0.2f, h * 0.55f, w / 2, h * 0.15f)
            close()
        }
        drawPath(
            path = path,
            color = color
        )
    }
}

@Composable
fun InjectionIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        rotate(45f) {
            drawLine(
                color = color,
                start = Offset(w / 2, h * 0.05f),
                end = Offset(w / 2, h * 0.3f),
                strokeWidth = 1.5.dp.toPx()
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(w * 0.4f, h * 0.3f),
                size = Size(w * 0.2f, h * 0.45f),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
            drawLine(
                color = color,
                start = Offset(w / 2, h * 0.75f),
                end = Offset(w / 2, h * 0.9f),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = color,
                start = Offset(w * 0.35f, h * 0.9f),
                end = Offset(w * 0.65f, h * 0.9f),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
