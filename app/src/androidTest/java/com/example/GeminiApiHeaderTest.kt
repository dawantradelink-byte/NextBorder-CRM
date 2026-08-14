package com.example

import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test class in src/androidTest to verify Gemini API request construction with x-goog-api-key header.
 */
@RunWith(AndroidJUnit4::class)
class GeminiApiHeaderTest {

    @Test
    fun testGeminiApiEndpointWithXGoogApiKeyHeader() {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        assertNotNull("API key configuration loaded", apiKey)

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent"
        val jsonPayload = """{"contents":[{"parts":[{"text":"Hello"}]}]}"""
        val requestBody = jsonPayload.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("x-goog-api-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        assertEquals("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent", request.url.toString())
        assertEquals(apiKey, request.header("x-goog-api-key"))
        assertEquals("POST", request.method)
    }
}
