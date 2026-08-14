package com.example.core.ai

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AiEngineRequest(
    val prompt: String,
    val systemInstruction: String? = null,
    val modelName: String = "gemini-1.5-flash-latest",
    val temperature: Float = 0.7f,
    val parameters: Map<String, Any> = emptyMap()
)

data class AiEngineResponse(
    val success: Boolean,
    val text: String,
    val rawJson: String? = null,
    val error: String? = null,
    val executionTimeMs: Long = 0L
)

interface AiEngine {
    suspend fun processRequest(request: AiEngineRequest): AiEngineResponse
    fun getLogs(): List<String>
}

object CentralAiEngine : AiEngine {
    private val executionLogs = mutableListOf<String>()

    override suspend fun processRequest(request: AiEngineRequest): AiEngineResponse {
        val startTime = System.currentTimeMillis()
        log("AI ENGINE REQUEST [Model: ${request.modelName}]: ${request.prompt.take(100)}...")
        
        if (!request.systemInstruction.isNullOrEmpty()) {
            log("System Instruction: ${request.systemInstruction!!.take(60)}...")
        }

        val response = CentralGeminiService.generateContent(
            prompt = request.prompt,
            systemInstruction = request.systemInstruction,
            modelName = request.modelName,
            temperature = request.temperature
        )

        val duration = System.currentTimeMillis() - startTime
        log("AI ENGINE RESPONSE [Time: ${duration}ms, Success: ${response.success}]")
        return response
    }

    override fun getLogs(): List<String> = executionLogs.toList()

    fun log(message: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val entry = "[$timestamp] [AI_ENGINE] $message"
        executionLogs.add(entry)
        AiCoordinator.log(entry)
        if (executionLogs.size > 500) executionLogs.removeAt(0)
    }
}
