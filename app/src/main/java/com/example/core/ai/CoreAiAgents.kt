package com.example.core.ai

class CeoAiAgent : BaseAiAgent(
    role = AiAgentRole.CEO_AI,
    name = "CEO AI Agent",
    description = "Oversees executive strategy, partnership goals, and overall OS operational health."
) {
    override suspend fun onExecute(context: AgentContext): AgentExecutionResult {
        val prompt = AiPromptBuilder.create()
            .setRoleContext("Chief Executive Officer AI")
            .setObjective("Synthesize organizational health and set priority directives.")
            .addParameter("taskId", context.taskId)
            .build()

        val response = callCentralAiEngine(prompt)
        return AgentExecutionResult(
            success = response.success,
            agentRole = role,
            summary = "CEO AI generated strategic directive for task ${context.taskId}",
            data = mapOf("directive" to response.text)
        )
    }
}

class UniversityResearchAiAgent : BaseAiAgent(
    role = AiAgentRole.UNIVERSITY_RESEARCH_AI,
    name = "University Research AI Agent",
    description = "Analyzes global university programs, intake deadlines, and tuition statistics."
) {
    override suspend fun onExecute(context: AgentContext): AgentExecutionResult {
        val prompt = AiPromptBuilder.create()
            .setRoleContext("Global Education Researcher")
            .setObjective("Analyze target university requirements and partner intake capacity.")
            .addParameter("taskId", context.taskId)
            .build()

        val response = callCentralAiEngine(prompt)
        return AgentExecutionResult(
            success = response.success,
            agentRole = role,
            summary = "Research completed for task ${context.taskId}",
            data = mapOf("researchOutput" to response.text)
        )
    }
}

class LeadHunterAiAgent : BaseAiAgent(
    role = AiAgentRole.LEAD_HUNTER_AI,
    name = "Lead Hunter AI Agent",
    description = "Identifies potential university partners, contact persons, and outreach opportunities."
) {
    override suspend fun onExecute(context: AgentContext): AgentExecutionResult {
        val prompt = AiPromptBuilder.create()
            .setRoleContext("B2B Education Partnerships Lead Hunter")
            .setObjective("Scan global education directories for unrepresented universities.")
            .addParameter("taskId", context.taskId)
            .build()

        val response = callCentralAiEngine(prompt)
        return AgentExecutionResult(
            success = response.success,
            agentRole = role,
            summary = "Lead Hunter identified partner candidates for task ${context.taskId}",
            data = mapOf("leads" to response.text)
        )
    }
}

class EmailAiAgent : BaseAiAgent(
    role = AiAgentRole.EMAIL_AI,
    name = "Email AI Agent",
    description = "Generates personalized, high-conversion cold outreach emails for university international offices."
) {
    override suspend fun onExecute(context: AgentContext): AgentExecutionResult {
        val prompt = AiPromptBuilder.create()
            .setRoleContext("Professional B2B Email Strategist")
            .setObjective("Craft non-spammy, tailored partnership pitch emails.")
            .addParameter("taskId", context.taskId)
            .build()

        val response = callCentralAiEngine(prompt)
        return AgentExecutionResult(
            success = response.success,
            agentRole = role,
            summary = "Email AI generated email draft for task ${context.taskId}",
            data = mapOf("emailDraft" to response.text)
        )
    }
}

class AnalyticsAiAgent : BaseAiAgent(
    role = AiAgentRole.ANALYTICS_AI,
    name = "Analytics & Rejection AI Agent",
    description = "Calculates conversion pipelines, rejection reasons, and greylist recovery strategies."
) {
    override suspend fun onExecute(context: AgentContext): AgentExecutionResult {
        val prompt = AiPromptBuilder.create()
            .setRoleContext("Data Analytics & Conversion Optimization AI")
            .setObjective("Analyze refusal patterns and recommend recovery timeline.")
            .addParameter("taskId", context.taskId)
            .build()

        val response = callCentralAiEngine(prompt)
        return AgentExecutionResult(
            success = response.success,
            agentRole = role,
            summary = "Analytics AI computed KPI metrics for task ${context.taskId}",
            data = mapOf("analyticsSummary" to response.text)
        )
    }
}

object CoreAiAgentInitializer {
    fun initializeDefaultAgents() {
        AiCoordinator.registerAgent(CeoAiAgent())
        AiCoordinator.registerAgent(UniversityResearchAiAgent())
        AiCoordinator.registerAgent(LeadHunterAiAgent())
        AiCoordinator.registerAgent(EmailAiAgent())
        AiCoordinator.registerAgent(AnalyticsAiAgent())
    }
}
