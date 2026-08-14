package com.example.util

import android.content.Context
import android.widget.Toast
import com.example.data.University
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader

object CsvUtils {

    fun exportUniversitiesToCsv(context: Context, universities: List<University>): File? {
        val file = File(context.cacheDir, "next_border_universities_export.csv")
        try {
            FileOutputStream(file).use { out ->
                val writer = out.bufferedWriter()
                // Header
                writer.write("Name,ContactName,Email,Country,City,Status,Priority,PartnershipStatus,IntakeMonths,CommissionRate,TuitionFees,Website,WhatsAppNumber,Tags,Notes\n")

                universities.forEach { u ->
                    val line = listOf(
                        escapeCsv(u.name),
                        escapeCsv(u.contactName),
                        escapeCsv(u.email),
                        escapeCsv(u.country),
                        escapeCsv(u.city),
                        escapeCsv(u.status),
                        escapeCsv(u.priority),
                        escapeCsv(u.partnershipStatus),
                        escapeCsv(u.intakeMonths),
                        escapeCsv(u.commissionRate),
                        escapeCsv(u.tuitionFees),
                        escapeCsv(u.website),
                        escapeCsv(u.whatsappNumber),
                        escapeCsv(u.tags),
                        escapeCsv(u.notes)
                    ).joinToString(",")
                    writer.write("$line\n")
                }
                writer.flush()
            }
            Toast.makeText(context, "Exported ${universities.size} universities to CSV", Toast.LENGTH_SHORT).show()
            return file
        } catch (e: Exception) {
            Toast.makeText(context, "CSV export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun parseCsvInputStream(inputStream: InputStream): List<University> {
        val list = mutableListOf<University>()
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            var isHeader = true
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                if (isHeader) {
                    isHeader = false
                    continue
                }
                val row = line ?: continue
                val tokens = row.split(",").map { it.trim().removeSurrounding("\"") }
                if (tokens.isNotEmpty() && tokens[0].isNotBlank()) {
                    val name = tokens.getOrElse(0) { "" }
                    val contactName = tokens.getOrElse(1) { "" }
                    val email = tokens.getOrElse(2) { "" }
                    val country = tokens.getOrElse(3) { "United Kingdom" }
                    val city = tokens.getOrElse(4) { "" }
                    val status = tokens.getOrElse(5) { "New" }
                    val priority = tokens.getOrElse(6) { "Medium" }
                    val partnershipStatus = tokens.getOrElse(7) { "Prospect" }
                    val intakeMonths = tokens.getOrElse(8) { "Jan, Sep" }
                    val commissionRate = tokens.getOrElse(9) { "15%" }
                    val tuitionFees = tokens.getOrElse(10) { "" }
                    val website = tokens.getOrElse(11) { "" }
                    val whatsapp = tokens.getOrElse(12) { "+447700900077" }
                    val tags = tokens.getOrElse(13) { "Middle-Class" }
                    val notes = tokens.getOrElse(14) { "" }

                    list.add(
                        University(
                            name = name,
                            contactName = contactName,
                            email = email,
                            country = country,
                            city = city,
                            status = status,
                            priority = priority,
                            partnershipStatus = partnershipStatus,
                            intakeMonths = intakeMonths,
                            commissionRate = commissionRate,
                            tuitionFees = tuitionFees,
                            website = website,
                            whatsappNumber = whatsapp,
                            tags = tags,
                            notes = notes
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun escapeCsv(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"${value.replace("\"", "\"\"")}\""
        }
        return value
    }
}
