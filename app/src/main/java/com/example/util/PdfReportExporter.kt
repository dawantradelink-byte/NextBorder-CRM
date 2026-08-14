package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.University
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportExporter {

    fun exportUniversitySummaryPdf(context: Context, university: University): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        var yPosition = 40f

        try {
            // Header
            paint.color = Color.parseColor("#3B1C58")
            canvas.drawRect(0f, 0f, 595f, 90f, paint)

            paint.color = Color.WHITE
            paint.textSize = 22f
            paint.isFakeBoldText = true
            canvas.drawText("NEXT BORDER VISA CONSULTANCY", 30f, 45f, paint)

            paint.textSize = 12f
            paint.isFakeBoldText = false
            paint.color = Color.parseColor("#FF8A00")
            canvas.drawText("B2B University Partnership Profile Report", 30f, 68f, paint)

            yPosition = 120f
            paint.color = Color.BLACK
            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas.drawText(university.name, 30f, yPosition, paint)

            yPosition += 25f
            paint.textSize = 11f
            paint.isFakeBoldText = false
            paint.color = Color.DKGRAY

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            canvas.drawText("Generated on: ${dateFormat.format(Date())}", 30f, yPosition, paint)

            yPosition += 30f
            drawDivider(canvas, yPosition)
            yPosition += 20f

            // Details
            val details = listOf(
                "Country / City" to "${university.country} / ${if (university.city.isBlank()) "N/A" else university.city}",
                "Primary Contact" to "${university.contactName} (${university.email})",
                "Partnership Status" to university.partnershipStatus,
                "Priority Level" to university.priority,
                "Assigned Counselor" to university.assignedCounselor,
                "WhatsApp Number" to university.whatsappNumber,
                "Tuition Fees" to if (university.tuitionFees.isBlank()) "N/A" else university.tuitionFees,
                "Commission Rate" to university.commissionRate,
                "Application Fee" to university.applicationFee,
                "Intake Months" to university.intakeMonths,
                "Target Category" to university.tags,
                "Accepts B2B Agents" to if (university.acceptsAgents) "Yes" else "No",
                "MOI Accepted" to if (university.moiAccepted) "Yes" else "No",
                "BBA / MBA Available" to "${if (university.bbaAvailable) "BBA " else ""}${if (university.mbaAvailable) "MBA" else ""}".ifBlank { "N/A" },
                "Scholarships Available" to if (university.scholarshipsAvailable) "Yes" else "No"
            )

            paint.textSize = 12f
            details.forEach { (label, value) ->
                if (yPosition > 780f) return@forEach
                paint.isFakeBoldText = true
                paint.color = Color.parseColor("#3B1C58")
                canvas.drawText("$label:", 30f, yPosition, paint)

                paint.isFakeBoldText = false
                paint.color = Color.BLACK
                canvas.drawText(value, 200f, yPosition, paint)
                yPosition += 22f
            }

            if (university.notes.isNotBlank() && yPosition < 750f) {
                yPosition += 15f
                paint.isFakeBoldText = true
                paint.color = Color.parseColor("#3B1C58")
                canvas.drawText("Notes & Criteria:", 30f, yPosition, paint)

                yPosition += 20f
                paint.isFakeBoldText = false
                paint.color = Color.BLACK
                val lines = wrapText(university.notes, paint, 530f)
                lines.forEach { line ->
                    if (yPosition <= 800f) {
                        canvas.drawText(line, 30f, yPosition, paint)
                        yPosition += 16f
                    }
                }
            }

            // Footer
            paint.color = Color.GRAY
            paint.textSize = 9f
            canvas.drawText("Next Border Visa Consultancy - CEO: Ishak Dewan - Confidentially Generated", 30f, 820f, paint)

            pdfDocument.finishPage(page)

            val file = File(context.cacheDir, "University_${university.id}_Report.pdf")
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            openPdf(context, file)
            return file
        } catch (e: Exception) {
            Toast.makeText(context, "Error exporting PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        } finally {
            pdfDocument.close()
        }
    }

    private fun drawDivider(canvas: android.graphics.Canvas, y: Float) {
        val paint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        canvas.drawLine(30f, y, 565f, y, paint)
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        return lines
    }

    private fun openPdf(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Open PDF Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "PDF saved to ${file.name}", Toast.LENGTH_SHORT).show()
        }
    }
}
