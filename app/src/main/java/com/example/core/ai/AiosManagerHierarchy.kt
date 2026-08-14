package com.example.core.ai

import com.example.core.log.AiosLogger
import com.example.core.log.LogCategory

enum class AiosManagerBranch {
    RESEARCH_MANAGER,
    MARKETING_MANAGER,
    STUDENT_MANAGER,
    SYSTEM_MANAGER
}

object ResearchManager {
    val branch = AiosManagerBranch.RESEARCH_MANAGER
    val subAgents = listOf(
        AiAgentRole.UNIVERSITY_RESEARCH_AI,
        AiAgentRole.UK_PARTNERSHIP_AI,
        AiAgentRole.KNOWLEDGE_BASE_AI
    )

    fun dispatchResearchTask(taskName: String): String {
        AiosLogger.log(LogCategory.AI_CALL, "ResearchManager", "Dispatching research task: $taskName across UK Universities, Colleges, Scholarships & News AI")
        return "Research task '$taskName' dispatched to Research Branch agents."
    }
}

object MarketingManager {
    val branch = AiosManagerBranch.MARKETING_MANAGER
    val subAgents = listOf(
        AiAgentRole.LEAD_HUNTER_AI,
        AiAgentRole.EMAIL_AI,
        AiAgentRole.WHATSAPP_AI,
        AiAgentRole.MEETING_AI
    )

    fun dispatchMarketingCampaign(campaignName: String): String {
        AiosLogger.log(LogCategory.AI_CALL, "MarketingManager", "Dispatching campaign: $campaignName across Lead Hunter, Email, Meeting & CRM AI")
        return "Marketing campaign '$campaignName' initiated across Marketing Branch agents."
    }
}

object StudentManager {
    val branch = AiosManagerBranch.STUDENT_MANAGER
    val subAgents = listOf(
        AiAgentRole.ADMISSION_AI,
        AiAgentRole.VISA_AI,
        AiAgentRole.DOCUMENT_AI,
        AiAgentRole.NOTIFICATION_AI
    )

    fun dispatchStudentProcessing(studentId: String): String {
        AiosLogger.log(LogCategory.AI_CALL, "StudentManager", "Dispatching student file processing for ID $studentId across Admission, Visa, Document & Follow-up AI")
        return "Student file $studentId assigned to Student Branch agents."
    }
}

object SystemManager {
    val branch = AiosManagerBranch.SYSTEM_MANAGER
    val subAgents = listOf(
        AiAgentRole.ANALYTICS_AI,
        AiAgentRole.MEMORY_AI,
        AiAgentRole.FINANCE_AI
    )

    fun performSystemMaintenance(): String {
        AiosLogger.log(LogCategory.AI_CALL, "SystemManager", "Performing System Maintenance & Memory/Analytics/Security sync")
        return "System Manager optimization cycle completed."
    }
}
