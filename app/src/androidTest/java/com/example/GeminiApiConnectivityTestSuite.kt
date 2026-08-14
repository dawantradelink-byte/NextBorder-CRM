package com.example

import android.util.Log
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
import java.util.concurrent.TimeUnit

/**
 * Automated connectivity test suite for Google Gemini API integration.
 * Tests live endpoint response using x-goog-api-key authentication without exposing credentials.
 */
@RunWith(AndroidJUnit4::class)
class GeminiApiConnectivityTestSuite {

    private val tag = "GeminiConnectivityTest"
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    @Test
    fun testGeminiApiLiveConnectivityWithHeader() {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        assertNotNull("Gemini API key must be configured in BuildConfig", apiKey)
        assertTrue("Gemini API key must not be blank", apiKey.isNotBlank())

        Log.i(tag, "Initiating Gemini API connectivity test...")
        Log.i(tag, "Target Model: gemini-1.5-flash-latest")
        Log.i(tag, "Auth Method: x-goog-api-key header [REDACTED]")

        val endpointUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
        val payload = """
            {
                "contents": [
                    {
                        "parts": [
                            {"text": "Ping"}
                        ]
                    }
                ]
            }
        """.trimIndent()

        val requestBody = payload.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(endpointUrl)
            .addHeader("x-goog-api-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val statusCode = response.code
        val responseBodyString = response.body?.string() ?: ""

        Log.i(tag, "Gemini API Response Status Code: $statusCode")
        Log.i(tag, "Response Body Length: ${responseBodyString.length}")
        
        if (response.isSuccessful) {
            Log.i(tag, "Connectivity Test PASSED: Successfully communicated with Gemini API.")
        } else {
            Log.e(tag, "Connectivity Test FAILED with Status $statusCode: $responseBodyString")
        }

        assertEquals("Expected HTTP 200 OK from Gemini API", 200, statusCode)
        assertTrue("Response body should contain candidates", responseBodyString.contains("candidates"))
    }
}
