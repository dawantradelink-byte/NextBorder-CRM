package com.example.core.ai

enum class AiAgentRole {
    CEO_AI,
    UNIVERSITY_RESEARCH_AI,
    UK_PARTNERSHIP_AI,
    MARKETING_AI,
    LEAD_HUNTER_AI,
    ADMISSION_AI,
    VISA_AI,
    DOCUMENT_AI,
    OCR_AI,
    EMAIL_AI,
    WHATSAPP_AI,
    MEETING_AI,
    ANALYTICS_AI,
    FINANCE_AI,
    TRANSLATION_AI,
    NOTIFICATION_AI,
    MEMORY_AI,
    KNOWLEDGE_BASE_AI
}

enum class AgentStatus {
    IDLE,
    PROCESSING,
    SUCCESS,
    FAILED,
    SUSPENDED
}

data class AgentContext(
    val taskId: String,
    val payload: Map<String, Any> = emptyMap(),
    val language: String = "en",
    val timestamp: Long = System.currentTimeMillis()
)

data class AgentExecutionResult(
    val success: Boolean,
    val agentRole: AiAgentRole,
    val summary: String,
    val data: Map<String, Any> = emptyMap(),
    val error: String? = null
)

interface AiAgent {
    val role: AiAgentRole
    val name: String
    val description: String
    fun getStatus(): AgentStatus
    suspend fun execute(context: AgentContext): AgentExecutionResult
}

object AiCoordinator {
    private val agentRegistry = mutableMapOf<AiAgentRole, AiAgent>()
    private val agentLogs = mutableListOf<String>()

    fun registerAgent(agent: AiAgent) {
        agentRegistry[agent.role] = agent
        log("Registered AI Agent: ${agent.name} [${agent.role}]")
    }

    fun getAgent(role: AiAgentRole): AiAgent? = agentRegistry[role]

    fun getAllAgents(): List<AiAgent> = agentRegistry.values.toList()

    suspend fun dispatchTask(role: AiAgentRole, context: AgentContext): AgentExecutionResult {
        val agent = agentRegistry[role]
            ?: return AgentExecutionResult(
                success = false,
                agentRole = role,
                summary = "Agent not registered: $role",
                error = "Unregistered agent"
            )
        log("Dispatching task [${context.taskId}] to ${agent.name}")
        val result = agent.execute(context)
        log("Agent ${agent.name} finished task. Result: ${if (result.success) "SUCCESS" else "FAILED"}")
        return result
    }

    fun log(message: String) {
        val entry = "[${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}] $message"
        agentLogs.add(entry)
        if (agentLogs.size > 500) agentLogs.removeAt(0)
    }

    fun getLogs(): List<String> = agentLogs.toList()
}
