package com.example.core.ai

import com.example.core.log.AiosLogger
import com.example.core.log.LogCategory
import java.util.UUID

enum class TaskPriority {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

data class CeoTaskDirective(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val targetRole: AiAgentRole,
    val priority: TaskPriority,
    val payload: Map<String, Any> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)

data class BusinessReport(
    val generatedAt: Long = System.currentTimeMillis(),
    val activePartnershipsCount: Int,
    val pendingEmailOutreachCount: Int,
    val agentHealthScore: Float,
    val strategicRecommendations: List<String>
)

object CeoOrchestrator {
    private val taskQueue = mutableListOf<CeoTaskDirective>()
    private val completedTasks = mutableSetOf<String>()
    private val activeAgentTasks = mutableMapOf<AiAgentRole, String>()

    fun submitTaskDirective(directive: CeoTaskDirective): Boolean {
        val taskHash = "${directive.targetRole}_${directive.title.hashCode()}"
        if (completedTasks.contains(taskHash)) {
            AiosLogger.log(LogCategory.AI_CALL, "CeoOrchestrator", "Prevented duplicate task submission: ${directive.title}")
            return false
        }
        synchronized(taskQueue) {
            taskQueue.add(directive)
            taskQueue.sortByDescending { it.priority.ordinal }
        }
        AiosLogger.log(LogCategory.AI_CALL, "CeoOrchestrator", "Submitted CEO Directive [Priority: ${directive.priority}]: ${directive.title}")
        return true
    }

    suspend fun processNextTask(): AgentExecutionResult? {
        val directive = synchronized(taskQueue) {
            if (taskQueue.isEmpty()) null else taskQueue.removeAt(0)
        } ?: return null

        activeAgentTasks[directive.targetRole] = directive.id
        AiosLogger.log(LogCategory.AI_CALL, "CeoOrchestrator", "Dispatching queued task '${directive.title}' to ${directive.targetRole}")

        val result = AiCoordinator.dispatchTask(
            role = directive.targetRole,
            context = AgentContext(
                taskId = directive.id,
                payload = directive.payload
            )
        )

        activeAgentTasks.remove(directive.targetRole)
        val taskHash = "${directive.targetRole}_${directive.title.hashCode()}"
        if (result.success) {
            completedTasks.add(taskHash)
        }
        return result
    }

    fun generateComprehensiveBusinessReport(activeCount: Int, pendingEmails: Int): BusinessReport {
        val recommendations = listOf(
            "Target intake capacity expansion for London-based UK Universities.",
            "Schedule automated follow-ups for non-responsive international office emails.",
            "Optimize database local cache for offline operation on Motorola Edge 60 Pro."
        )
        return BusinessReport(
            activePartnershipsCount = activeCount,
            pendingEmailOutreachCount = pendingEmails,
            agentHealthScore = 98.5f,
            strategicRecommendations = recommendations
        )
    }
}
