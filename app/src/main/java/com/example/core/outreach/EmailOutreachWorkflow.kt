package com.example.core.outreach

import com.example.core.log.AiosLogger
import com.example.core.log.LogCategory
import java.util.UUID

enum class OutreachStatus {
    RESEARCHED,
    DRAFT_PREPARED,
    USER_APPROVED,
    SENT,
    FOLLOW_UP_SCHEDULED,
    REPLIED,
    CLOSED
}

data class OutreachRecord(
    val id: String = UUID.randomUUID().toString(),
    val institutionName: String,
    val contactEmail: String,
    val initialDraft: String,
    val status: OutreachStatus = OutreachStatus.DRAFT_PREPARED,
    val followUpDelayDays: Int = 3,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

object EmailOutreachWorkflow {
    private val records = mutableMapOf<String, OutreachRecord>()

    fun createOutreachDraft(institutionName: String, contactEmail: String, draftText: String): OutreachRecord {
        val record = OutreachRecord(
            institutionName = institutionName,
            contactEmail = contactEmail,
            initialDraft = draftText,
            status = OutreachStatus.DRAFT_PREPARED
        )
        records[record.id] = record
        AiosLogger.log(LogCategory.AI_CALL, "EmailOutreachWorkflow", "Created email draft for $institutionName ($contactEmail)")
        return record
    }

    fun updateStatus(recordId: String, newStatus: OutreachStatus) {
        records[recordId]?.let { current ->
            val updated = current.copy(status = newStatus, lastUpdated = System.currentTimeMillis())
            records[recordId] = updated
            AiosLogger.log(LogCategory.AI_CALL, "EmailOutreachWorkflow", "Updated outreach ${current.institutionName} status to $newStatus")
        }
    }

    fun getPendingFollowUps(): List<OutreachRecord> {
        val now = System.currentTimeMillis()
        return records.values.filter {
            it.status == OutreachStatus.SENT && (now - it.lastUpdated > it.followUpDelayDays * 86400000L)
        }
    }

    fun getAllOutreachRecords(): List<OutreachRecord> = records.values.toList()
}
