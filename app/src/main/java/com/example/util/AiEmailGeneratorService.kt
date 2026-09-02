package com.example.util

import com.example.data.BusinessSettings
import com.example.data.University
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AiEmailGeneratorService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateOutreachEmail(
        university: University,
        templateType: String,
        tone: String,
        settings: BusinessSettings,
        extraInstruction: String = ""
    ): GeneratedEmailResult = withContext(Dispatchers.IO) {
        val apiKey = com.example.core.ai.CentralGeminiService.getApiKey()
        val subject = buildSubject(templateType, university, settings)

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            val fallbackBody = generateFallbackBody(templateType, tone, university, settings, extraInstruction)
            return@withContext GeneratedEmailResult(
                subject = subject,
                body = fallbackBody,
                isAiGenerated = false
            )
        }

        try {
            val prompt = """
                You are writing an official, high-converting B2B student recruitment partnership email for Next Border.
                
                SENDER DETAILS:
                - Name: ${settings.ceoName}
                - Role: CEO, Next Border
                - Background: USA Graduate with 3 years of successful experience in international student recruitment and visa processing.
                - WhatsApp: ${settings.whatsappNumber}
                - Official Email: ${settings.officialEmail}
                - Legal Support: We collaborate with an independent immigration lawyer for legal & compliance guidance.
                - Track Record: USA student visas (with/without scholarship), Italy & Finland student visas, and tourist visas where appropriate.
                
                TARGET INSTITUTION:
                - University: ${university.name}
                - Contact Person: ${if (university.contactName.isNotBlank()) university.contactName else "International Recruitment Team"}
                - Country: ${university.country}
                - Intakes: ${university.intakeMonths}
                - Programs: BBA: ${if (university.bbaAvailable) "Yes" else "No"}, MBA/MSc: ${if (university.mbaAvailable) "Yes" else "No"}
                
                EMAIL GOAL:
                Request to become an official/approved student recruitment agent for ${university.name} and inquire about their B2B agent onboarding process / agreement.
                
                COPYWRITING & SAFETY RULES (STRICT):
                - Do NOT exaggerate or make false claims.
                - NEVER guarantee visa approvals or say "we guarantee success".
                - Keep tone professional, warm, human, specific, and concise.
                - Tone requested: $tone
                - Email type: $templateType
                - Additional notes: $extraInstruction
                - Include a polite opt-out line at the end ("If you are not the right contact or not accepting new agents, please let us know.").
                
                Format the email cleanly and conclude with this signature block:
                Best regards,

                ${settings.ceoName}
                CEO, Next Border
                WhatsApp: ${settings.whatsappNumber}
            """.trimIndent()

            val response = com.example.core.ai.CentralGeminiService.generateContent(
                prompt = prompt,
                systemInstruction = "You are a professional B2B email strategist writing high-converting university partnership emails.",
                modelName = com.example.core.ai.CentralGeminiService.DEFAULT_MODEL
            )

            if (response.success && response.text.isNotBlank()) {
                return@withContext GeneratedEmailResult(
                    subject = subject,
                    body = ValidationUtils.cleanTextEncoding(response.text.trim()),
                    isAiGenerated = true
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Return fallback if API fails or credentials empty
        val fallbackBody = generateFallbackBody(templateType, tone, university, settings, extraInstruction)
        return@withContext GeneratedEmailResult(
            subject = subject,
            body = fallbackBody,
            isAiGenerated = false
        )
    }

    private fun buildSubject(templateType: String, u: University, settings: BusinessSettings): String {
        return when (templateType) {
            "First Outreach" -> "Student Recruitment Partnership Inquiry with Next Border"
            "Follow-up 1" -> "Re: Student Recruitment Partnership Inquiry — ${u.name}"
            "Follow-up 2" -> "Re: B2B Agent Partnership Opportunity with Next Border"
            "Meeting Request" -> "Brief Intro Call: Next Border & ${u.name} Agent Onboarding"
            "Partnership Proposal" -> "B2B Agent Partnership Proposal (${u.name})"
            "Thank You" -> "Thank You for Meeting — Next Steps with Next Border"
            "Reactivation" -> "Reconnecting: Upcoming Intakes for ${u.name}"
            else -> "Student Recruitment Collaboration Inquiry — Next Border"
        }
    }

    private fun generateFallbackBody(
        templateType: String,
        tone: String,
        u: University,
        s: BusinessSettings,
        extra: String
    ): String {
        val contact = if (u.contactName.isNotBlank()) u.contactName else "International Recruitment Team"

        val text = StringBuilder()
        text.append("Dear $contact,\n\n")
        text.append("I hope this email finds you well.\n\n")

        when (templateType) {
            "First Outreach", "Partnership Proposal" -> {
                text.append("My name is ${s.ceoName}, CEO of Next Border. I am a USA graduate and have been working successfully in the student recruitment and visa processing field for the last 3 years.\n\n")
                text.append("At Next Border, we support genuine students with university selection, application guidance, documentation, and visa preparation. We have experience with USA student visa cases, including both scholarship and non-scholarship pathways, and we also work with students applying to Italy and Finland. In addition, we support tourist visa processing where appropriate, and we work separately with an independent immigration lawyer for immigration-related guidance.\n\n")
                text.append("I am reaching out to explore whether ${u.name} is currently open to working with trusted international recruitment partners or B2B agents. We would be interested in discussing an official agent agreement and learning more about your requirements for representing your institution professionally.\n\n")
                text.append("Our goal is to connect serious, well-prepared students with universities where they can succeed academically and personally. I would appreciate the opportunity to schedule a short call or receive guidance on your agent onboarding process.\n\n")
            }
            "Follow-up 1" -> {
                text.append("I am following up on my previous message regarding prospective B2B recruitment collaboration between Next Border and ${u.name}.\n\n")
                text.append("We currently have well-prepared candidates seeking degree programs in ${u.country} for upcoming intakes (${u.intakeMonths}). We take pride in screening students thoroughly prior to application.\n\n")
                text.append("Could you kindly share if ${u.name} is accepting new agent applications, or let us know the appropriate portal or contact for agent onboarding?\n\n")
            }
            "Meeting Request" -> {
                text.append("I would love to request a brief 10-15 minute online meeting with your international partnerships team.\n\n")
                text.append("We would welcome the chance to introduce Next Border's recruitment track record and explore an official agent partnership with ${u.name}.\n\n")
            }
            else -> {
                text.append("I am writing to express Next Border's strong interest in representing ${u.name} for international student admissions.\n\n")
                text.append("As an agency led by a USA graduate with 3 years of experience in student and visa counseling, we prioritize genuine student selection and high compliance.\n\n")
            }
        }

        if (extra.isNotBlank()) {
            text.append("Additional Note: $extra\n\n")
        }

        text.append("Thank you for your time, and I look forward to hearing from you.\n\n")
        text.append("If you are not the right contact or not currently adding new agent partners, please let us know so we can update our records.\n\n")
        text.append("Best regards,\n\n")
        text.append("${s.ceoName}\n")
        text.append("CEO, Next Border\n")
        text.append("WhatsApp: ${s.whatsappNumber}")

        return ValidationUtils.cleanTextEncoding(text.toString())
    }
}

data class GeneratedEmailResult(
    val subject: String,
    val body: String,
    val isAiGenerated: Boolean
)

