package com.example.core.ai

import android.content.Context
import com.example.BuildConfig
import com.example.core.log.AiosLogger
import com.example.core.log.LogCategory
import com.example.core.security.SecureStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class AiHealthStatus {
    CONNECTED,
    TESTING,
    ERROR,
    NO_KEY
}

data class AiHealthState(
    val status: AiHealthStatus = AiHealthStatus.TESTING,
    val isOnline: Boolean = false,
    val activeModel: String = "gemini-1.5-flash-latest",
    val apiKeyConfigured: Boolean = false,
    val accountEmail: String = "dawan.tradelink@gmail.com",
    val lastCheckTime: Long = 0L,
    val lastLatencyMs: Long = 0L,
    val totalRequests: Int = 0,
    val successCount: Int = 0,
    val failureCount: Int = 0,
    val lastError: String? = null
)

data class GeminiChatMessagePair(
    val role: String, // "user" or "model"
    val text: String
)

object CentralGeminiService {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
    private const val KEY_GEMINI_API = "GEMINI_API_KEY_SECURE"
    const val DEFAULT_MODEL = "gemini-1.5-flash-latest"
    const val PRO_MODEL = "gemini-1.5-flash-latest"
    const val FALLBACK_MODEL = "gemini-1.5-flash-latest"

    private fun normalizeModelName(rawModel: String): String {
        val model = if (rawModel.isBlank()) DEFAULT_MODEL else rawModel
        return if (model == "gemini-1.5-flash-latest" || model.contains("gemini-1.5")) {
            "gemini-2.5-flash"
        } else {
            model
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        val sanitizedMessage = message
            .replace(Regex("([?&]key=)[^&\\s]+"), "$1[REDACTED]")
            .replace(Regex("(x-goog-api-key:\\s*)[^\\s]+", RegexOption.IGNORE_CASE), "$1[REDACTED]")
            .replace(Regex("(Authorization:\\s*)[^\\s]+", RegexOption.IGNORE_CASE), "$1[REDACTED]")
        AiosLogger.log(LogCategory.NETWORK, "CentralGeminiService", sanitizedMessage)
        CentralAiEngine.log("Network: $sanitizedMessage")
    }.apply {
        level = HttpLoggingInterceptor.Level.HEADERS
        redactHeader("x-goog-api-key")
        redactHeader("Authorization")
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val _healthState = MutableStateFlow(AiHealthState())
    val healthState: StateFlow<AiHealthState> = _healthState.asStateFlow()

    private var cachedApiKey: String = ""

    fun sanitizeApiKey(rawKey: String?): String {
        if (rawKey.isNullOrBlank()) return ""
        var key = rawKey.trim()
        // Strip leading/trailing double or single quotes
        if ((key.startsWith("\"") && key.endsWith("\"")) || (key.startsWith("'") && key.endsWith("'"))) {
            if (key.length >= 2) {
                key = key.substring(1, key.length - 1).trim()
            }
        }
        // Strip Bearer prefix if accidentally present
        if (key.startsWith("Bearer ", ignoreCase = true)) {
            key = key.substring(7).trim()
        }
        // Strip quotes/whitespace again
        key = key.trim('"', '\'', ' ', '\n', '\r', '\t')
        return key
    }

    fun getApiKey(context: Context? = null): String {
        val cleanCached = sanitizeApiKey(cachedApiKey)
        if (cleanCached.isNotBlank() && cleanCached != "MY_GEMINI_API_KEY") {
            return cleanCached
        }

        if (context != null) {
            val storedKey = SecureStorageManager.getEncryptedString(context, KEY_GEMINI_API, "")
            val cleanStored = sanitizeApiKey(storedKey)
            if (cleanStored.isNotBlank() && cleanStored != "MY_GEMINI_API_KEY") {
                cachedApiKey = cleanStored
                return cleanStored
            }
        }

        val buildConfigKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        val cleanBuildConfig = sanitizeApiKey(buildConfigKey)
        if (cleanBuildConfig.isNotBlank() && cleanBuildConfig != "MY_GEMINI_API_KEY") {
            cachedApiKey = cleanBuildConfig
            if (context != null) {
                SecureStorageManager.saveEncryptedString(context, KEY_GEMINI_API, cleanBuildConfig)
            }
            return cleanBuildConfig
        }

        return ""
    }

    fun setApiKey(context: Context, key: String) {
        val cleaned = sanitizeApiKey(key)
        cachedApiKey = cleaned
        SecureStorageManager.saveEncryptedString(context, KEY_GEMINI_API, cleaned)
        val isConfigured = cleaned.isNotBlank() && cleaned != "MY_GEMINI_API_KEY"
        _healthState.value = _healthState.value.copy(
            apiKeyConfigured = isConfigured,
            status = if (!isConfigured) AiHealthStatus.NO_KEY else _healthState.value.status
        )
    }

    suspend fun verifyConnectivity(context: Context): Boolean = withContext(Dispatchers.IO) {
        _healthState.value = _healthState.value.copy(status = AiHealthStatus.TESTING)
        val apiKey = getApiKey(context)

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            _healthState.value = _healthState.value.copy(
                status = AiHealthStatus.NO_KEY,
                isOnline = false,
                apiKeyConfigured = false,
                lastCheckTime = System.currentTimeMillis(),
                lastError = "API Key not configured in Secrets panel"
            )
            AiosLogger.log(LogCategory.SECURITY, "GeminiService", "Connectivity check failed: API Key missing or placeholder.")
            return@withContext false
        }

        val startTime = System.currentTimeMillis()
        val engineRequest = AiEngineRequest(
            prompt = "Ping test: Reply with 'PONG'",
            systemInstruction = "You are a network ping responder. Output only PONG.",
            modelName = DEFAULT_MODEL
        )

        val response = generateContentInternal(apiKey = apiKey, request = engineRequest, maxRetries = 1)
        val latency = System.currentTimeMillis() - startTime

        if (response.success && response.text.contains("PONG", ignoreCase = true)) {
            _healthState.value = _healthState.value.copy(
                status = AiHealthStatus.CONNECTED,
                isOnline = true,
                apiKeyConfigured = true,
                lastCheckTime = System.currentTimeMillis(),
                lastLatencyMs = latency,
                lastError = null
            )
            AiosLogger.log(LogCategory.SECURITY, "GeminiService", "Gemini API Health Check PASSED (${latency}ms)")
            return@withContext true
        } else {
            val err = response.error ?: "Ping response invalid"
            _healthState.value = _healthState.value.copy(
                status = AiHealthStatus.ERROR,
                isOnline = false,
                apiKeyConfigured = true,
                lastCheckTime = System.currentTimeMillis(),
                lastLatencyMs = latency,
                lastError = err
            )
            AiosLogger.log(LogCategory.SECURITY, "GeminiService", "Gemini API Health Check FAILED: $err")
            return@withContext false
        }
    }

    suspend fun generateContent(
        context: Context? = null,
        prompt: String,
        systemInstruction: String? = null,
        modelName: String = DEFAULT_MODEL,
        history: List<GeminiChatMessagePair> = emptyList(),
        temperature: Float = 0.7f
    ): AiEngineResponse = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(context)
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            _healthState.value = _healthState.value.copy(
                status = AiHealthStatus.NO_KEY,
                isOnline = false,
                apiKeyConfigured = false,
                lastError = "Gemini API Key missing"
            )
            return@withContext AiEngineResponse(
                success = false,
                text = "",
                error = "Gemini API Key is missing or not configured in Secrets panel. Please configure GEMINI_API_KEY."
            )
        }

        val engineRequest = AiEngineRequest(
            prompt = prompt,
            systemInstruction = systemInstruction,
            modelName = modelName,
            temperature = temperature,
            parameters = mapOf("history" to history)
        )

        generateContentInternal(apiKey = apiKey, request = engineRequest)
    }

    fun generateContentStream(
        context: Context? = null,
        prompt: String,
        systemInstruction: String? = null,
        modelName: String = DEFAULT_MODEL,
        history: List<GeminiChatMessagePair> = emptyList()
    ): Flow<String> = flow {
        val rawApiKey = getApiKey(context)
        val apiKey = sanitizeApiKey(rawApiKey)
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            emit("Error: Gemini API key is missing or not configured in Secrets panel.")
            return@flow
        }

        val targetModel = normalizeModelName(modelName)
        val url = "$BASE_URL$targetModel:streamGenerateContent?key=$apiKey"

        CentralAiEngine.log("GeminiService [Stream]: Key loaded: PRESENT (Length ${apiKey.length}, API Key)")
        CentralAiEngine.log("GeminiService [Stream]: Endpoint URL: $BASE_URL$targetModel:streamGenerateContent?key=[REDACTED] | Model: $targetModel")

        val requestJson = buildJsonPayload(prompt, systemInstruction, history, 0.7f)
        val body = requestJson.toString().toRequestBody("application/json".toMediaType())

        val builder = Request.Builder()
            .url(url)
            .post(body)

        val httpRequest = builder.build()

        try {
            val response = httpClient.newCall(httpRequest).execute()
            if (response.isSuccessful) {
                response.body?.byteStream()?.bufferedReader()?.use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val trimmed = line?.trim() ?: continue
                        if (trimmed.startsWith("data:")) {
                            val jsonStr = trimmed.removePrefix("data:").trim()
                            if (jsonStr.isNotBlank() && jsonStr != "[DONE]") {
                                try {
                                    val json = JSONObject(jsonStr)
                                    val textChunk = parseTextFromResponseJson(json)
                                    if (textChunk.isNotBlank()) {
                                        emit(textChunk)
                                    }
                                } catch (_: Exception) {}
                            }
                        } else if (trimmed.startsWith("{")) {
                            try {
                                val json = JSONObject(trimmed)
                                val textChunk = parseTextFromResponseJson(json)
                                if (textChunk.isNotBlank()) {
                                    emit(textChunk)
                                }
                            } catch (_: Exception) {}
                        }
                    }
                }
            } else {
                val errText = response.body?.string() ?: response.message
                emit("Error: Stream failed with status code ${response.code} ($errText)")
            }
        } catch (e: Exception) {
            emit("Error in stream: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun generateContentInternal(
        apiKey: String,
        request: AiEngineRequest,
        maxRetries: Int = 3
    ): AiEngineResponse {
        val startTime = System.currentTimeMillis()
        val cleanKey = sanitizeApiKey(apiKey)

        CentralAiEngine.log("GeminiService: API Key loaded: ${if (cleanKey.isNotBlank()) "PRESENT (Length: ${cleanKey.length}, Type: API Key)" else "MISSING/PLACEHOLDER"}")

        var currentModel = normalizeModelName(request.modelName)

        @Suppress("UNCHECKED_CAST")
        val history = request.parameters["history"] as? List<GeminiChatMessagePair> ?: emptyList()

        val payload = buildJsonPayload(
            prompt = request.prompt,
            systemInstruction = request.systemInstruction,
            history = history,
            temperature = request.temperature
        )

        val requestBody = payload.toString().toRequestBody("application/json".toMediaType())
        var attempt = 0
        var backoffMs = 1000L

        while (attempt < maxRetries) {
            attempt++
            val url = "$BASE_URL$currentModel:generateContent?key=$cleanKey"
            val redactedUrl = "$BASE_URL$currentModel:generateContent?key=[REDACTED]"

            CentralAiEngine.log("GeminiService (Attempt $attempt/$maxRetries): Endpoint URL: $redactedUrl | Model: $currentModel")

            try {
                val reqBuilder = Request.Builder()
                    .url(url)
                    .post(requestBody)

                val httpRequest = reqBuilder.build()

                val httpResponse = httpClient.newCall(httpRequest).execute()
                val duration = System.currentTimeMillis() - startTime
                val responseBodyStr = httpResponse.body?.string() ?: ""

                if (httpResponse.isSuccessful) {
                    val json = JSONObject(responseBodyStr)
                    val textResult = parseTextFromResponseJson(json)

                    if (textResult.isNotBlank()) {
                        updateHealthMetrics(success = true, durationMs = duration)
                        CentralAiEngine.log("GeminiService: Received successful response in ${duration}ms [Text Length: ${textResult.length}]")
                        return AiEngineResponse(
                            success = true,
                            text = textResult,
                            rawJson = responseBodyStr,
                            executionTimeMs = duration
                        )
                    } else {
                        val finishReason = json.optJSONArray("candidates")?.optJSONObject(0)?.optString("finishReason", "UNKNOWN")
                        val err = "Empty response content from Gemini (FinishReason: $finishReason)"
                        CentralAiEngine.log("GeminiService: $err")
                        updateHealthMetrics(success = false, durationMs = duration, error = err)
                        return AiEngineResponse(
                            success = false,
                            text = "",
                            rawJson = responseBodyStr,
                            error = err,
                            executionTimeMs = duration
                        )
                    }
                } else if (httpResponse.code == 400 && currentModel != FALLBACK_MODEL && attempt == 1) {
                    CentralAiEngine.log("GeminiService: HTTP 400 with model '$currentModel'. Retrying with fallback model '$FALLBACK_MODEL'...")
                    currentModel = FALLBACK_MODEL
                    delay(500L)
                    continue
                } else if (httpResponse.code == 429) {
                    CentralAiEngine.log("GeminiService: Rate limited (429). Retry attempt $attempt/$maxRetries after ${backoffMs}ms...")
                    if (attempt < maxRetries) {
                        delay(backoffMs)
                        backoffMs *= 2
                        continue
                    } else {
                        val err = "Gemini API Rate Limit Exceeded (429). Please wait before making more requests."
                        updateHealthMetrics(success = false, durationMs = duration, error = err)
                        return AiEngineResponse(
                            success = false,
                            text = "",
                            error = err,
                            executionTimeMs = duration
                        )
                    }
                } else {
                    val err = "HTTP Error ${httpResponse.code}: ${httpResponse.message} - $responseBodyStr"
                    CentralAiEngine.log("GeminiService: $err")
                    if (attempt < maxRetries && httpResponse.code >= 500) {
                        delay(backoffMs)
                        backoffMs *= 2
                        continue
                    }
                    updateHealthMetrics(success = false, durationMs = duration, error = err)
                    return AiEngineResponse(
                        success = false,
                        text = "",
                        rawJson = responseBodyStr,
                        error = err,
                        executionTimeMs = duration
                    )
                }
            } catch (e: Exception) {
                val duration = System.currentTimeMillis() - startTime
                CentralAiEngine.log("GeminiService Exception (Attempt $attempt/$maxRetries): ${e.message}")
                if (attempt < maxRetries) {
                    delay(backoffMs)
                    backoffMs *= 2
                } else {
                    updateHealthMetrics(success = false, durationMs = duration, error = e.message ?: "Network error")
                    return AiEngineResponse(
                        success = false,
                        text = "",
                        error = "Gemini Network Exception: ${e.message}",
                        executionTimeMs = duration
                    )
                }
            }
        }

        val duration = System.currentTimeMillis() - startTime
        return AiEngineResponse(
            success = false,
            text = "",
            error = "Gemini API failed after $maxRetries attempts.",
            executionTimeMs = duration
        )
    }

    private fun buildJsonPayload(
        prompt: String,
        systemInstruction: String?,
        history: List<GeminiChatMessagePair>,
        temperature: Float
    ): JSONObject {
        val root = JSONObject()

        if (!systemInstruction.isNullOrBlank()) {
            root.put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemInstruction))
                })
            })
        }

        val contentsArray = JSONArray()

        history.forEach { pair ->
            contentsArray.put(JSONObject().apply {
                put("role", if (pair.role.contains("user", ignoreCase = true) || pair.role.contains("Founder", ignoreCase = true)) "user" else "model")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", pair.text))
                })
            })
        }

        contentsArray.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
                put(JSONObject().put("text", prompt))
            })
        })

        root.put("contents", contentsArray)

        root.put("generationConfig", JSONObject().apply {
            put("temperature", temperature)
            put("topP", 0.95)
        })

        return root
    }

    private fun parseTextFromResponseJson(json: JSONObject): String {
        val candidates = json.optJSONArray("candidates") ?: return ""
        if (candidates.length() == 0) return ""
        val candidate = candidates.optJSONObject(0) ?: return ""
        val content = candidate.optJSONObject("content") ?: return ""
        val parts = content.optJSONArray("parts") ?: return ""

        val sb = StringBuilder()
        for (i in 0 until parts.length()) {
            val part = parts.optJSONObject(i)
            val text = part?.optString("text", "") ?: ""
            if (text.isNotEmpty()) sb.append(text)
        }
        return sb.toString()
    }

    private fun updateHealthMetrics(success: Boolean, durationMs: Long, error: String? = null) {
        val current = _healthState.value
        val total = current.totalRequests + 1
        val successes = if (success) current.successCount + 1 else current.successCount
        val failures = if (!success) current.failureCount + 1 else current.failureCount
        val newStatus = if (success) AiHealthStatus.CONNECTED else if (current.apiKeyConfigured) AiHealthStatus.ERROR else AiHealthStatus.NO_KEY

        _healthState.value = current.copy(
            status = newStatus,
            isOnline = success,
            lastLatencyMs = durationMs,
            totalRequests = total,
            successCount = successes,
            failureCount = failures,
            lastError = if (!success) error else current.lastError
        )
    }
}
