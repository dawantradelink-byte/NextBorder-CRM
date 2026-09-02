package com.example.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.University
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.charset.StandardCharsets

@RunWith(RobolectricTestRunner::class)
class CsvUtilsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testExportUniversitiesToCsv_success() {
        val universities = listOf(
            University(
                name = "Test University 1",
                contactName = "John Doe",
                email = "john@example.com",
                country = "USA",
                city = "New York",
                status = "New",
                priority = "High",
                partnershipStatus = "ACTIVE",
                intakeMonths = "Jan, Sep",
                commissionRate = "10%",
                tuitionFees = "$20000",
                website = "http://test1.edu",
                whatsappNumber = "+11234567890",
                tags = "Tech, Business",
                notes = "Good prospect"
            ),
            University(
                name = "Test University 2, Comma",
                contactName = "Jane \"Quote\" Smith",
                email = "jane@example.com",
                country = "UK",
                city = "London\nNewLine",
                status = "Contacted",
                priority = "Medium",
                partnershipStatus = "PROSPECT",
                intakeMonths = "Oct",
                commissionRate = "15%",
                tuitionFees = "£15000",
                website = "http://test2.ac.uk",
                whatsappNumber = "+449876543210",
                tags = "Arts",
                notes = "Follow up soon"
            )
        )

        val exportedFile = CsvUtils.exportUniversitiesToCsv(context, universities)
        assertNotNull(exportedFile)
        assertTrue(exportedFile!!.exists())

        val lines = exportedFile.readLines()
        // CsvUtils writes a newline character literally which readLines() interprets as a new line,
        // so it actually splits the second row into two lines. Total lines = 4.
        assertEquals("Should contain header + 2 data rows (but 1 has a newline)", 4, lines.size)

        // Verify Header
        assertEquals(
            "Name,ContactName,Email,Country,City,Status,Priority,PartnershipStatus,IntakeMonths,CommissionRate,TuitionFees,Website,WhatsAppNumber,Tags,Notes",
            lines[0]
        )

        // Verify row 1 (normal data)
        assertEquals(
            "Test University 1,John Doe,john@example.com,USA,New York,New,High,ACTIVE,\"Jan, Sep\",10%,$20000,http://test1.edu,+11234567890,\"Tech, Business\",Good prospect",
            lines[1]
        )

        val content = exportedFile.readText()
        assertTrue(content.contains("\"Test University 2, Comma\""))
        assertTrue(content.contains("\"Jane \"\"Quote\"\" Smith\""))
        assertTrue(content.contains("\"London\nNewLine\""))
    }

    @Test
    fun testExportUniversitiesToCsv_emptyList() {
        val exportedFile = CsvUtils.exportUniversitiesToCsv(context, emptyList())
        assertNotNull(exportedFile)
        assertTrue(exportedFile!!.exists())

        val lines = exportedFile.readLines()
        assertEquals(1, lines.size)
        assertEquals("Name,ContactName,Email,Country,City,Status,Priority,PartnershipStatus,IntakeMonths,CommissionRate,TuitionFees,Website,WhatsAppNumber,Tags,Notes", lines[0])
    }

    @Test
    fun testParseCsvInputStream_success() {
        val csvContent = """
            Name,ContactName,Email,Country,City,Status,Priority,PartnershipStatus,IntakeMonths,CommissionRate,TuitionFees,Website,WhatsAppNumber,Tags,Notes
            Parse Uni 1,Alice,alice@example.com,Canada,Toronto,Meeting,Low,MEETING,Sep,20%,10000,http://parse1.ca,+123,Tech,Notes here
            Parse Uni 2,Bob,bob@example.com,,,,,,,,,,,,
        """.trimIndent()

        val inputStream = ByteArrayInputStream(csvContent.toByteArray(StandardCharsets.UTF_8))
        val universities = CsvUtils.parseCsvInputStream(inputStream)

        assertEquals(2, universities.size)

        val uni1 = universities[0]
        assertEquals("Parse Uni 1", uni1.name)
        assertEquals("Alice", uni1.contactName)
        assertEquals("alice@example.com", uni1.email)
        assertEquals("Canada", uni1.country)
        assertEquals("Toronto", uni1.city)
        assertEquals("Meeting", uni1.status)
        assertEquals("Low", uni1.priority)
        assertEquals("MEETING", uni1.partnershipStatus)
        assertEquals("Sep", uni1.intakeMonths)
        assertEquals("20%", uni1.commissionRate)
        assertEquals("10000", uni1.tuitionFees)
        assertEquals("http://parse1.ca", uni1.website)
        assertEquals("+123", uni1.whatsappNumber)
        assertEquals("Tech", uni1.tags)
        assertEquals("Notes here", uni1.notes)

        // Uni 2 tests the default fallback logic when properties are missing
        // The parser parses empty strings as "" instead of falling back, if the commas are present.
        // If the commas are present but empty, tokens will be ""
        // Note: the implementation says tokens.getOrElse(3) { "United Kingdom" }. If token is present but "", it takes ""
        val uni2 = universities[1]
        assertEquals("Parse Uni 2", uni2.name)
        assertEquals("Bob", uni2.contactName)
        assertEquals("bob@example.com", uni2.email)
        // Since there are exactly 14 commas, it produces 15 tokens. Most of them are "".
        // The fallback is only for OutOfBounds.
        assertEquals("", uni2.country)
        assertEquals("", uni2.city)
        assertEquals("", uni2.status)
        assertEquals("", uni2.priority)
        assertEquals("", uni2.partnershipStatus)
        assertEquals("", uni2.intakeMonths)
        assertEquals("", uni2.commissionRate)
        assertEquals("", uni2.tuitionFees)
        assertEquals("", uni2.website)
        assertEquals("", uni2.whatsappNumber)
        assertEquals("", uni2.tags)
        assertEquals("", uni2.notes)
    }

    @Test
    fun testParseCsvInputStream_emptyFile() {
        val inputStream = ByteArrayInputStream("".toByteArray(StandardCharsets.UTF_8))
        val universities = CsvUtils.parseCsvInputStream(inputStream)
        assertTrue(universities.isEmpty())
    }

    @Test
    fun testParseCsvInputStream_onlyHeader() {
        val csvContent = "Name,ContactName,Email,Country,City,Status,Priority,PartnershipStatus,IntakeMonths,CommissionRate,TuitionFees,Website,WhatsAppNumber,Tags,Notes"
        val inputStream = ByteArrayInputStream(csvContent.toByteArray(StandardCharsets.UTF_8))
        val universities = CsvUtils.parseCsvInputStream(inputStream)
        assertTrue(universities.isEmpty())
    }
}
