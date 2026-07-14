package com.dosemate.android.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.dosemate.android.presentation.screens.history.HistoryDashboardData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper to generate a PDF report of medication adherence for doctors.
 */
object PdfExportHelper {

    fun exportHistoryToPdf(context: Context, data: HistoryDashboardData): File? {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint().apply {
            textSize = 18f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            textSize = 12f
        }

        // Create a single page (A4-ish)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        var y = 40f
        canvas.drawText("MedDose Adherence Report", 40f, y, titlePaint)
        y += 30f
        
        canvas.drawText("Range: ${data.timeRange}", 40f, y, bodyPaint)
        y += 20f
        canvas.drawText("Overall Adherence: ${(data.adherenceRate * 100).toInt()}%", 40f, y, bodyPaint)
        y += 40f

        canvas.drawText("Daily Logs:", 40f, y, titlePaint)
        y += 25f

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        data.logsByDay.toSortedMap(reverseOrder()).forEach { (dayMillis, logs) ->
            if (y > 750) { // Simple pagination check
                pdfDocument.finishPage(page)
                // In a real app, we'd start a new page here. For MVP, we'll stop.
                return@forEach 
            }
            
            canvas.drawText(dateFormat.format(Date(dayMillis)), 40f, y, bodyPaint.apply { isFakeBoldText = true })
            y += 20f
            bodyPaint.isFakeBoldText = false
            
            logs.forEach { log ->
                val statusText = log.status.name
                canvas.drawText("- ${log.medicationName}: $statusText", 60f, y, bodyPaint)
                y += 15f
            }
            y += 10f
        }

        pdfDocument.finishPage(page)

        // Save to Downloads folder
        val fileName = "MedDose_Report_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        
        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
