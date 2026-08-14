package com.example.core.ai

import com.example.core.log.AiosLogger

class AiPromptBuilder private constructor() {
    private var roleContext: String = ""
    private var objective: String = ""
    private val inputParameters = mutableMapOf<String, String>()
    private val constraints = mutableListOf<String>()

    fun setRoleContext(role: String): AiPromptBuilder {
        this.roleContext = role
        return this
    }

    fun setObjective(objective: String): AiPromptBuilder {
        this.objective = objective
        return this
    }

    fun addParameter(key: String, value: String): AiPromptBuilder {
        this.inputParameters[key] = value
        return this
    }

    fun addConstraint(constraint: String): AiPromptBuilder {
        this.constraints.add(constraint)
        return this
    }

    fun build(): String {
        val sb = StringBuilder()
        if (roleContext.isNotBlank()) {
            sb.append("ROLE: ").append(roleContext).append("\n\n")
        }
        if (objective.isNotBlank()) {
            sb.append("OBJECTIVE: ").append(objective).append("\n\n")
        }
        if (inputParameters.isNotEmpty()) {
            sb.append("INPUT CONTEXT:\n")
            inputParameters.forEach { (k, v) ->
                sb.append("- ").append(k).append(": ").append(v).append("\n")
            }
            sb.append("\n")
        }
        if (constraints.isNotEmpty()) {
            sb.append("CONSTRAINTS & RULES:\n")
            constraints.forEach { c ->
                sb.append("1. ").append(c).append("\n")
            }
        }
        return sb.toString()
    }

    companion object {
        fun create(): AiPromptBuilder = AiPromptBuilder()
    }
}

data class AiRetryConfig(
    val maxRetries: Int = 3,
    val initialDelayMs: Long = 1000L,
    val backoffFactor: Double = 2.0
)

object AiRetryHandler {
    suspend fun <T> executeWithRetry(
        config: AiRetryConfig = AiRetryConfig(),
        actionName: String,
        block: suspend () -> T
    ): Result<T> {
        var currentDelay = config.initialDelayMs
        var lastException: Throwable? = null

        for (attempt in 1..config.maxRetries) {
            try {
                AiosLogger.ai("AiRetryHandler", "Executing $actionName (Attempt $attempt/${config.maxRetries})")
                val result = block()
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e
                AiosLogger.error("AiRetryHandler", "Attempt $attempt failed for $actionName: ${e.message}")
                if (attempt < config.maxRetries) {
                    kotlinx.coroutines.delay(currentDelay)
                    currentDelay = (currentDelay * config.backoffFactor).toLong()
                }
            }
        }
        return Result.failure(lastException ?: Exception("Action $actionName failed after ${config.maxRetries} attempts"))
    }
}

object AiHealthMonitor {
    private var totalRequests: Long = 0
    private var successfulRequests: Long = 0
    private var failedRequests: Long = 0
    private var totalExecutionTimeMs: Long = 0

    fun recordSuccess(durationMs: Long) {
        synchronized(this) {
            totalRequests++
            successfulRequests++
            totalExecutionTimeMs += durationMs
        }
    }

    fun recordFailure() {
        synchronized(this) {
            totalRequests++
            failedRequests++
        }
    }

    fun getHealthStats(): Map<String, Any> {
        synchronized(this) {
            val avgTime = if (successfulRequests > 0) totalExecutionTimeMs / successfulRequests else 0L
            val successRate = if (totalRequests > 0) (successfulRequests.toDouble() / totalRequests.toDouble()) * 100.0 else 100.0
            return mapOf(
                "totalRequests" to totalRequests,
                "successfulRequests" to successfulRequests,
                "failedRequests" to failedRequests,
                "successRatePercent" to String.format("%.1f", successRate),
                "averageResponseTimeMs" to avgTime
            )
        }
    }
}
