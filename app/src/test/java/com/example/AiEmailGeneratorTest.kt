package com.example

import com.example.data.BusinessSettings
import com.example.data.University
import com.example.util.AiEmailGeneratorService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AiEmailGeneratorTest {

    @Test
    fun testFallbackEmailGeneratorIncludesRequiredMetadata() = runBlocking {
        val uni = University(
            name = "University of Portsmouth",
            contactName = "Sarah Jenkins",
            email = "partners@port.ac.uk",
            country = "United Kingdom",
            intlPercentage = "25"
        )
        val settings = BusinessSettings(
            agencyName = "Next Border",
            ceoName = "Ishak Dewan",
            whatsappNumber = "+447700900077"
        )

        val draft = AiEmailGeneratorService.generateOutreachEmail(
            university = uni,
            templateType = "First Outreach",
            tone = "Persuasive",
            settings = settings
        )

        assertTrue(draft.subject.contains("University of Portsmouth") || draft.subject.contains("Next Border"))
        assertTrue(draft.body.contains("Ishak Dewan"))
        assertTrue(draft.body.contains("Next Border"))
        assertTrue(draft.body.contains("+447700900077"))
    }
}
