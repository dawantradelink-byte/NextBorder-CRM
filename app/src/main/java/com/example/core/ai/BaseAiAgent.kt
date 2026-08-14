package com.example.core.ai

abstract class BaseAiAgent(
    override val role: AiAgentRole,
    override val name: String,
    override val description: String
) : AiAgent {

    private var currentStatus: AgentStatus = AgentStatus.IDLE

    override fun getStatus(): AgentStatus = currentStatus

    protected fun setStatus(status: AgentStatus) {
        this.currentStatus = status
    }

    protected suspend fun callCentralAiEngine(
        prompt: String,
        systemInstruction: String? = null,
        modelName: String = "gemini-1.5-flash-latest"
    ): AiEngineResponse {
        CentralAiEngine.log("[$name] Invoking Central AI Engine with prompt length ${prompt.length}")
        return CentralAiEngine.processRequest(
            AiEngineRequest(
                prompt = prompt,
                systemInstruction = systemInstruction ?: "You are $name, an autonomous AI agent in NextBorder AIOS.",
                modelName = modelName
            )
        )
    }

    override suspend fun execute(context: AgentContext): AgentExecutionResult {
        setStatus(AgentStatus.PROCESSING)
        CentralAiEngine.log("[$name] Started execution for Task ID: ${context.taskId}")
        
        return try {
            val result = onExecute(context)
            setStatus(if (result.success) AgentStatus.SUCCESS else AgentStatus.FAILED)
            CentralAiEngine.log("[$name] Execution finished for Task ID: ${context.taskId}, Success: ${result.success}")
            result
        } catch (e: Exception) {
            setStatus(AgentStatus.FAILED)
            CentralAiEngine.log("[$name] Execution exception for Task ID: ${context.taskId}: ${e.message}")
            AgentExecutionResult(
                success = false,
                agentRole = role,
                summary = "Execution failed with exception",
                error = e.message ?: "Unknown error"
            )
        }
    }

    abstract suspend fun onExecute(context: AgentContext): AgentExecutionResult
}
