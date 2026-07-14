package com.dosemate.android.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A custom bar chart showing adherence over a period of time.
 */
@Composable
fun AdherenceChart(
    dailyRates: Map<Long, Float>, // Date (millis) -> Rate (0.0 to 1.0)
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val dayFormat = SimpleDateFormat("E", Locale.getDefault())

    val sortedData = dailyRates.toSortedMap().toList().takeLast(7)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 8.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = 32.dp.toPx()
            val spacing = (canvasWidth - (barWidth * sortedData.size)) / (sortedData.size + 1)

            sortedData.forEachIndexed { index, (_, rate) ->
                val x = spacing + index * (barWidth + spacing)
                val barHeight = canvasHeight * rate.coerceIn(0.05f, 1f)
                
                // Background track
                drawRoundRect(
                    color = surfaceVariant,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, canvasHeight),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                
                // Active bar
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(x, canvasHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            sortedData.forEach { (dateMillis, _) ->
                Text(
                    text = dayFormat.format(Date(dateMillis)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
