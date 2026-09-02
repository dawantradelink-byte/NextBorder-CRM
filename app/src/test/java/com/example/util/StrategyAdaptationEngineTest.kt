package com.example.util

import com.example.data.University
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StrategyAdaptationEngineTest {

    @Test
    fun testDiagnoseNonReplyUnder7Days() {
        val university = University(
            name = "Test University",
            contactName = "Test Contact",
            email = "test@university.edu"
        )
        val diagnosis = StrategyAdaptationEngine.diagnoseNonReply(university, 5L)

        assertEquals("Test University", diagnosis.universityName)
        assertTrue(diagnosis.probableReason.contains("under 7 days"))
        assertTrue(diagnosis.recommendedStrategy.contains("Gentle follow-up"))
        assertTrue(diagnosis.suggestedSubjectLine.contains("Quick Follow-Up"))
    }

    @Test
    fun testDiagnoseNonReplyBetween7And14Days() {
        val university = University(
            name = "Test University",
            contactName = "Test Contact",
            email = "test@university.edu"
        )
        val diagnosis = StrategyAdaptationEngine.diagnoseNonReply(university, 10L)

        assertEquals("Test University", diagnosis.universityName)
        assertTrue(diagnosis.probableReason.contains("Generic admissions contact"))
        assertTrue(diagnosis.recommendedStrategy.contains("Pivot approach"))
        assertTrue(diagnosis.suggestedSubjectLine.contains("Attn:"))
    }

    @Test
    fun testDiagnoseNonReplyOver14Days() {
        val university = University(
            name = "Test University",
            contactName = "Test Contact",
            email = "test@university.edu"
        )
        val diagnosis = StrategyAdaptationEngine.diagnoseNonReply(university, 20L)

        assertEquals("Test University", diagnosis.universityName)
        assertTrue(diagnosis.probableReason.contains("Primary inbox missed"))
        assertTrue(diagnosis.recommendedStrategy.contains("high-priority 3-line B2B re-engagement"))
        assertTrue(diagnosis.suggestedSubjectLine.contains("Re: Agent Onboarding Inquiry"))
    }

    @Test
    fun testRunDeliverabilityAuditWithValidEmail() {
        val email = "contact@example.edu"
        val audit = StrategyAdaptationEngine.runDeliverabilityAudit(email)

        assertTrue(audit.spfConfigured)
        assertTrue(audit.dkimConfigured)
        assertTrue(audit.dmarcPolicy.contains(email))
        assertTrue(audit.humanApprovalEnforced)
        assertTrue(audit.biometricLockActive)
    }

    @Test
    fun testRunDeliverabilityAuditWithoutAtSymbol() {
        val email = "invalidemailformat"
        val audit = StrategyAdaptationEngine.runDeliverabilityAudit(email)

        assertTrue(audit.spfConfigured)
        assertTrue(audit.dkimConfigured)
        assertTrue(audit.dmarcPolicy.contains(email))
    }

    @Test
    fun testGenerateInitialMockReplies() {
        val mockReplies = StrategyAdaptationEngine.generateInitialMockReplies()

        assertEquals(2, mockReplies.size)
        assertEquals("University of Greenwich", mockReplies[0].universityName)
        assertEquals("University of East London", mockReplies[1].universityName)
        assertTrue(mockReplies[0].receivedAt > 0)
        assertTrue(mockReplies[1].receivedAt > 0)
    }
}
