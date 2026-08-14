package com.example.util

import com.example.data.University

enum class RejectionType {
    TEMPORARY_GREYLIST,
    PERMANENT_BLACKLIST,
    UNKNOWN_REVIEW
}

data class RejectionAnalysis(
    val type: RejectionType,
    val targetStatus: String,
    val reason: String,
    val confidenceScore: Double,
    val suggestedFollowUpDateMs: Long,
    val recommendedStrategy: String,
    val aiAnalysis: String
)

object UniversityStatusEngine {

    val VALID_STATUSES = listOf(
        "ACTIVE",
        "CONTACTED",
        "FOLLOW_UP",
        "MEETING",
        "PARTNER",
        "COMPLETED",
        "NO_RESPONSE",
        "GREYLIST",
        "BLACKLIST"
    )

    fun mapToStandardStatus(statusInput: String): String {
        val s = statusInput.trim().uppercase()
        return when {
            s == "ACTIVE" || s == "PROSPECT" || s == "NEW" -> "ACTIVE"
            s == "CONTACTED" -> "CONTACTED"
            s == "FOLLOW_UP" || s == "FOLLOWUP1" || s == "FOLLOWUP2" -> "FOLLOW_UP"
            s == "MEETING" || s == "IN DISCUSSION" -> "MEETING"
            s == "PARTNER" || s == "PARTNERED" -> "PARTNER"
            s == "COMPLETED" || s == "ONBOARDED" -> "COMPLETED"
            s == "NO_RESPONSE" -> "NO_RESPONSE"
            s == "GREYLIST" || s == "ON HOLD" -> "GREYLIST"
            s == "BLACKLIST" -> "BLACKLIST"
            else -> "ACTIVE"
        }
    }

    fun analyzeRejectionReply(replySnippet: String, universityName: String): RejectionAnalysis {
        val text = replySnippet.lowercase()
        val now = System.currentTimeMillis()

        // Permanent Blacklist Triggers
        val permanentKeywords = listOf(
            "never contact us again",
            "remove us permanently",
            "remove from your list",
            "legal notice",
            "spam complaint",
            "policy violation",
            "do not email",
            "unsubscribe",
            "stop emailing",
            "take legal action"
        )

        for (kw in permanentKeywords) {
            if (text.contains(kw)) {
                return RejectionAnalysis(
                    type = RejectionType.PERMANENT_BLACKLIST,
                    targetStatus = "BLACKLIST",
                    reason = "Explicit permanent refusal or policy restriction ($kw)",
                    confidenceScore = 0.98,
                    suggestedFollowUpDateMs = 0L,
                    recommendedStrategy = "DO NOT CONTACT. Permanently blacklisted to maintain compliance and deliverability reputation.",
                    aiAnalysis = "High-risk permanent refusal detected. Future emails and follow-ups strictly blocked."
                )
            }
        }

        // Temporary Greylist Triggers
        val temporaryKeywords = mapOf(
            "not interested" to "General temporary disinterest",
            "already have an agent" to "Existing agency exclusivity",
            "not accepting new partners" to "Intake capacity ceiling reached",
            "do not require representation" to "In-house recruitment focus",
            "full capacity" to "Quota satisfied for current cycle",
            "closed applications" to "Application window closed"
        )

        for ((kw, desc) in temporaryKeywords) {
            if (text.contains(kw)) {
                // Default 6 months follow-up
                val sixMonthsMs = now + (180L * 24 * 3600 * 1000)
                return RejectionAnalysis(
                    type = RejectionType.TEMPORARY_GREYLIST,
                    targetStatus = "GREYLIST",
                    reason = "$desc ($kw)",
                    confidenceScore = 0.88,
                    suggestedFollowUpDateMs = sixMonthsMs,
                    recommendedStrategy = "Greylisted for 6 months. Review strategic positioning with updated CEO profile and student volume credentials before re-evaluating.",
                    aiAnalysis = "Temporary rejection detected. University moved to Greylist. Automated review queue active."
                )
            }
        }

        // Unknown / Manual Review
        val threeMonthsMs = now + (90L * 24 * 3600 * 1000)
        return RejectionAnalysis(
            type = RejectionType.UNKNOWN_REVIEW,
            targetStatus = "GREYLIST",
            reason = "Ambiguous rejection or neutral response requiring manual review",
            confidenceScore = 0.65,
            suggestedFollowUpDateMs = threeMonthsMs,
            recommendedStrategy = "Manual review required by Regional Director before any further contact.",
            aiAnalysis = "Sentiment ambiguous. Placed on Greylist for manual approval."
        )
    }

    fun sanitizeAndDeduplicateUniversities(universities: List<University>): List<University> {
        val seenKeys = mutableSetOf<String>()
        val cleanList = mutableListOf<University>()

        for (u in universities) {
            val nameKey = u.name.trim().lowercase()
            val emailKey = u.email.trim().lowercase()
            val primaryKey = if (emailKey.isNotBlank()) emailKey else nameKey

            if (!seenKeys.contains(primaryKey)) {
                seenKeys.add(primaryKey)
                // Clean formatting
                cleanList.add(
                    u.copy(
                        name = u.name.trim(),
                        email = u.email.trim().lowercase(),
                        website = u.website.trim().lowercase(),
                        country = u.country.trim(),
                        contactName = u.contactName.trim()
                    )
                )
            }
        }
        return cleanList
    }
}
