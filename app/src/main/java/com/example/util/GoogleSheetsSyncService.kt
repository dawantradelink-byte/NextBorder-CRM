package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.University
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object GoogleSheetsSyncService {

    const val GOOGLE_DRIVE_ACCOUNT = "dawan.tradelink@gmail.com"

    suspend fun exportToGoogleSheetsCsv(context: Context, rawUniversities: List<University>): File = withContext(Dispatchers.IO) {
        val universities = UniversityStatusEngine.sanitizeAndDeduplicateUniversities(rawUniversities)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "NextBorder_CRM_GoogleSheets_Master_$timeStamp.csv"
        
        val cacheDir = context.cacheDir
        val file = File(cacheDir, fileName)

        FileOutputStream(file).use { out ->
            val sb = StringBuilder()

            // Header Banner
            sb.append("# NEXT BORDER CRM — GOOGLE SHEETS MASTER DATABASE\n")
            sb.append("# Managed Account: $GOOGLE_DRIVE_ACCOUNT\n")
            sb.append("# Export Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n\n")

            // 1. Active Universities
            appendSheetSection(sb, "1. ACTIVE UNIVERSITIES", universities.filter { u ->
                val s = UniversityStatusEngine.mapToStandardStatus(u.partnershipStatus)
                s == "ACTIVE"
            })

            // 2. Contacted
            appendSheetSection(sb, "2. CONTACTED", universities.filter { u ->
                val s = UniversityStatusEngine.mapToStandardStatus(u.partnershipStatus)
                s == "CONTACTED"
            })

            // 3. Follow-Up Queue
            appendSheetSection(sb, "3. FOLLOW-UP QUEUE", universities.filter { u ->
                val s = UniversityStatusEngine.mapToStandardStatus(u.partnershipStatus)
                s == "FOLLOW_UP" || u.nextFollowUpDate > 0L
            })

            // 4. Meetings
            appendSheetSection(sb, "4. MEETINGS SCHEDULED", universities.filter { u ->
                val s = UniversityStatusEngine.mapToStandardStatus(u.partnershipStatus)
                s == "MEETING"
            })

            // 5. Partners
            appendSheetSection(sb, "5. PARTNERS (SIGNED B2B)", universities.filter { u ->
                val s = UniversityStatusEngine.mapToStandardStatus(u.partnershipStatus)
                s == "PARTNER" || s == "COMPLETED"
            })

            // 6. Greylist
            appendGreylistSection(sb, "6. GREYLIST (TEMPORARY REJECTIONS & REVIEW QUEUE)", universities.filter { u ->
                val s = UniversityStatusEngine.mapToStandardStatus(u.partnershipStatus)
                s == "GREYLIST"
            })

            // 7. Blacklist
            appendBlacklistSection(sb, "7. BLACKLIST (PERMANENT REFUSALS - DO NOT CONTACT)", universities.filter { u ->
                val s = UniversityStatusEngine.mapToStandardStatus(u.partnershipStatus)
                s == "BLACKLIST"
            })

            // 8. No Response
            appendSheetSection(sb, "8. NO RESPONSE", universities.filter { u ->
                val s = UniversityStatusEngine.mapToStandardStatus(u.partnershipStatus)
                s == "NO_RESPONSE"
            })

            // 9. Archive
            appendSheetSection(sb, "9. ARCHIVE", universities.filter { it.isArchived })

            // 10. Analytics & Performance Summary
            val total = universities.size.toDouble().coerceAtLeast(1.0)
            val partnersCount = universities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "PARTNER" }
            val greylistCount = universities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "GREYLIST" }
            val blacklistCount = universities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "BLACKLIST" }
            val meetingsCount = universities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "MEETING" }

            val acceptanceRate = (partnersCount / total) * 100
            val rejectionRate = ((greylistCount + blacklistCount) / total) * 100

            sb.append("\n=== 10. ANALYTICS & PIPELINE METRICS ===\n")
            sb.append("Metric,Value\n")
            sb.append("\"Total Leads In System\",${universities.size}\n")
            sb.append("\"Signed B2B Partners\",$partnersCount\n")
            sb.append("\"Greylisted Universities\",$greylistCount\n")
            sb.append("\"Blacklisted Universities\",$blacklistCount\n")
            sb.append("\"Scheduled Meetings\",$meetingsCount\n")
            sb.append("\"Acceptance Rate (%)\",\"${String.format("%.1f%%", acceptanceRate)}\"\n")
            sb.append("\"Rejection Rate (%)\",\"${String.format("%.1f%%", rejectionRate)}\"\n\n")

            // 11. Campaign History
            sb.append("=== 11. CAMPAIGN HISTORY ===\n")
            sb.append("Campaign Name,Target Region,Status,Universities Contacted,Conversion Rate\n")
            sb.append("\"UK January Intake Outreach 2026\",\"United Kingdom\",\"Completed\",15,\"20%\"\n")
            sb.append("\"London Region High-Commission Focus\",\"London, UK\",\"Active\",8,\"25%\"\n\n")

            // 12. Knowledge Base
            sb.append("=== 12. KNOWLEDGE BASE & STRATEGY LESSONS ===\n")
            sb.append("Category,Insight,Actionable Rule\n")
            sb.append("\"Rejection Response\",\"Temporary rejections ('not accepting agents') shift to 6m Greylist\",\"Never spam; re-pitch with updated CEO credentials and student cohort guarantees.\"\n")
            sb.append("\"Deliverability\",\"Use double opt-in and verified B2B signatures\",\"Ensure SPF/DKIM compliance for CEO Ishak Dewan outreach.\"\n")

            out.write(sb.toString().toByteArray(Charsets.UTF_8))
        }
        return@withContext file
    }

    private fun appendSheetSection(sb: StringBuilder, sectionTitle: String, list: List<University>) {
        sb.append("\n=== $sectionTitle (${list.size} Records) ===\n")
        sb.append("University Name,Country,City,Website,Contact Email,Contact Person,Status,Priority,Intakes,Assigned Counselor,Notes\n")
        list.forEach { u ->
            sb.append("\"${escapeCsv(u.name)}\",")
            sb.append("\"${escapeCsv(u.country)}\",")
            sb.append("\"${escapeCsv(u.city)}\",")
            sb.append("\"${escapeCsv(u.website)}\",")
            sb.append("\"${escapeCsv(u.email)}\",")
            sb.append("\"${escapeCsv(u.contactName)}\",")
            sb.append("\"${escapeCsv(u.partnershipStatus)}\",")
            sb.append("\"${escapeCsv(u.priority)}\",")
            sb.append("\"${escapeCsv(u.intakeMonths)}\",")
            sb.append("\"${escapeCsv(u.assignedCounselor)}\",")
            sb.append("\"${escapeCsv(u.notes)}\"\n")
        }
    }

    private fun appendGreylistSection(sb: StringBuilder, sectionTitle: String, list: List<University>) {
        sb.append("\n=== $sectionTitle (${list.size} Records) ===\n")
        sb.append("University Name,Country,Website,Contact Person,Contact Email,Refusal Date,Reason,Original Conversation,Confidence Score,Suggested FollowUp Date,Recommended Strategy,AI Analysis\n")
        list.forEach { u ->
            sb.append("\"${escapeCsv(u.name)}\",")
            sb.append("\"${escapeCsv(u.country)}\",")
            sb.append("\"${escapeCsv(u.website)}\",")
            sb.append("\"${escapeCsv(u.contactName)}\",")
            sb.append("\"${escapeCsv(u.email)}\",")
            sb.append("\"${if (u.greylistRefusalDate > 0) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(u.greylistRefusalDate)) else "N/A"}\",")
            sb.append("\"${escapeCsv(u.greylistReason)}\",")
            sb.append("\"${escapeCsv(u.greylistConversation)}\",")
            sb.append("\"${u.greylistConfidenceScore}\",")
            sb.append("\"${if (u.greylistSuggestedFollowUpDate > 0) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(u.greylistSuggestedFollowUpDate)) else "N/A"}\",")
            sb.append("\"${escapeCsv(u.greylistRecommendedStrategy)}\",")
            sb.append("\"${escapeCsv(u.greylistAiAnalysis)}\"\n")
        }
    }

    private fun appendBlacklistSection(sb: StringBuilder, sectionTitle: String, list: List<University>) {
        sb.append("\n=== $sectionTitle (${list.size} Records) ===\n")
        sb.append("University Name,Country,Website,Contact Person,Contact Email,Refusal Date,Permanent Blacklist Reason\n")
        list.forEach { u ->
            sb.append("\"${escapeCsv(u.name)}\",")
            sb.append("\"${escapeCsv(u.country)}\",")
            sb.append("\"${escapeCsv(u.website)}\",")
            sb.append("\"${escapeCsv(u.contactName)}\",")
            sb.append("\"${escapeCsv(u.email)}\",")
            sb.append("\"${if (u.blacklistDate > 0) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(u.blacklistDate)) else "N/A"}\",")
            sb.append("\"${escapeCsv(u.blacklistReason.ifBlank { "Explicit permanent refusal" })}\"\n")
        }
    }

    fun shareToGoogleDrive(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Next Border CRM Leads — Google Sheets Table (${GOOGLE_DRIVE_ACCOUNT})")
            putExtra(Intent.EXTRA_TEXT, "Next Border CRM Lead Data for Google Drive ($GOOGLE_DRIVE_ACCOUNT).\nImport this CSV directly into Google Sheets on Drive.")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Save Table to Google Drive ($GOOGLE_DRIVE_ACCOUNT)")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun escapeCsv(text: String): String {
        return text.replace("\"", "\"\"").replace("\n", " ")
    }
}
