package com.example.core.ai

import com.example.core.log.AiosLogger

enum class AgentCapabilityType {
    REASONING,
    WEB_SEARCH,
    SHEETS_SYNC,
    EMAIL_COMPOSER,
    DOCUMENT_OCR,
    REJECTION_ANALYSIS,
    AUTO_SCHEDULE,
    BI_REPORTING
}

data class AgentCapability(
    val name: String,
    val type: AgentCapabilityType,
    val description: String
)

data class AgentExecutionRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val agentRole: AiAgentRole,
    val agentName: String,
    val taskId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long,
    val success: Boolean,
    val summary: String,
    val error: String? = null
)

object AgentExecutionTracker {
    private val records = mutableListOf<AgentExecutionRecord>()
    private const val MAX_HISTORY = 500

    fun recordExecution(record: AgentExecutionRecord) {
        synchronized(records) {
            records.add(record)
            if (records.size > MAX_HISTORY) {
                records.removeAt(0)
            }
        }
        AiosLogger.ai(
            "AgentExecutionTracker",
            "Agent [${record.agentName}] completed task [${record.taskId}] in ${record.durationMs}ms with result: ${if (record.success) "SUCCESS" else "FAILED"}"
        )
    }

    fun getHistoryForAgent(role: AiAgentRole): List<AgentExecutionRecord> {
        synchronized(records) {
            return records.filter { it.agentRole == role }
        }
    }

    fun getAllRecords(): List<AgentExecutionRecord> {
        synchronized(records) {
            return records.toList()
        }
    }
}
