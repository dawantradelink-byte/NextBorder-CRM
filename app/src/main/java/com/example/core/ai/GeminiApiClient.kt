package com.example.core.ai

data class GeminiContentPart(val text: String? = null)
data class GeminiContent(val parts: List<GeminiContentPart>)
data class GeminiGenerateRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)
data class GeminiCandidate(val content: GeminiContent)
data class GeminiGenerateResponse(val candidates: List<GeminiCandidate>? = null)

interface GeminiApiClient {
    suspend fun generateContent(
        model: String = "gemini-1.5-flash-latest",
        prompt: String,
        systemInstruction: String? = null
    ): GeminiGenerateResponse
}

class DirectGeminiApiClient(
    private val apiKeySupplier: () -> String = { "" }
) : GeminiApiClient {

    override suspend fun generateContent(
        model: String,
        prompt: String,
        systemInstruction: String?
    ): GeminiGenerateResponse {
        val key = apiKeySupplier()
        CentralAiEngine.log("DirectGeminiApiClient: Model=$model, KeyPresent=${key.isNotBlank()}, PromptLength=${prompt.length}")
        
        val res = CentralGeminiService.generateContent(
            prompt = prompt,
            systemInstruction = systemInstruction,
            modelName = model
        )

        return GeminiGenerateResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(
                        parts = listOf(GeminiContentPart(text = res.text))
                    )
                )
            )
        )
    }
}
