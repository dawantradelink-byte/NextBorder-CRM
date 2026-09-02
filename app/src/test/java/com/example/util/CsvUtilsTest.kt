package com.example.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.University
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CsvUtilsTest {

    @Test
    fun testExportUniversitiesToCsv_HappyPath() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val university = University(
            name = "Test University",
            contactName = "John Doe",
            email = "john@test.edu",
            country = "United Kingdom",
            city = "London",
            status = "New",
            priority = "Medium",
            partnershipStatus = "Prospect",
            intakeMonths = "Jan Sep",
            commissionRate = "15%",
            tuitionFees = "10000",
            website = "www.test.edu",
            whatsappNumber = "+441234567890",
            tags = "Tag1",
            notes = "Some notes"
        )

        val file = CsvUtils.exportUniversitiesToCsv(context, listOf(university))

        assertNotNull(file)
        assertTrue(file!!.exists())

        val lines = file.readLines()
        assertEquals(2, lines.size) // Header + 1 data row

        // Check data row
        val dataRow = lines[1]
        val expectedRow = "Test University,John Doe,john@test.edu,United Kingdom,London,New,Medium,Prospect,Jan Sep,15%,10000,www.test.edu,+441234567890,Tag1,Some notes"
        assertEquals(expectedRow, dataRow)
    }

    @Test
    fun testExportUniversitiesToCsv_EdgeCase_Commas() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val university = University(
            name = "Test, University",
            contactName = "Doe, John",
            email = "john@test.edu",
            country = "United Kingdom",
            city = "London, UK",
            status = "New",
            priority = "Medium",
            partnershipStatus = "Prospect",
            intakeMonths = "Jan, Sep",
            commissionRate = "15%, 20%",
            tuitionFees = "10,000",
            website = "www.test.edu",
            whatsappNumber = "+441234567890",
            tags = "Tag1, Tag2",
            notes = "Note1, Note2"
        )

        val file = CsvUtils.exportUniversitiesToCsv(context, listOf(university))

        assertNotNull(file)
        assertTrue(file!!.exists())

        val lines = file.readLines()
        assertEquals(2, lines.size)

        val dataRow = lines[1]
        val expectedRow = "\"Test, University\",\"Doe, John\",john@test.edu,United Kingdom,\"London, UK\",New,Medium,Prospect,\"Jan, Sep\",\"15%, 20%\",\"10,000\",www.test.edu,+441234567890,\"Tag1, Tag2\",\"Note1, Note2\""
        assertEquals(expectedRow, dataRow)
    }

    @Test
    fun testExportUniversitiesToCsv_EdgeCase_Quotes() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val university = University(
            name = "Test \"University\"",
            contactName = "John \"The Man\" Doe",
            email = "john@test.edu",
            country = "United Kingdom",
            city = "London",
            status = "New",
            priority = "Medium",
            partnershipStatus = "Prospect",
            intakeMonths = "Jan",
            commissionRate = "15%",
            tuitionFees = "10000",
            website = "www.test.edu",
            whatsappNumber = "+441234567890",
            tags = "\"Tag\"",
            notes = "He said \"Hello\""
        )

        val file = CsvUtils.exportUniversitiesToCsv(context, listOf(university))

        assertNotNull(file)
        assertTrue(file!!.exists())

        val lines = file.readLines()
        assertEquals(2, lines.size)

        val dataRow = lines[1]
        val expectedRow = "\"Test \"\"University\"\"\",\"John \"\"The Man\"\" Doe\",john@test.edu,United Kingdom,London,New,Medium,Prospect,Jan,15%,10000,www.test.edu,+441234567890,\"\"\"Tag\"\"\",\"He said \"\"Hello\"\"\""
        assertEquals(expectedRow, dataRow)
    }

    @Test
    fun testExportUniversitiesToCsv_EdgeCase_Newlines() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val university = University(
            name = "Test\nUniversity",
            contactName = "John\nDoe",
            email = "john@test.edu",
            country = "United Kingdom",
            city = "London",
            status = "New",
            priority = "Medium",
            partnershipStatus = "Prospect",
            intakeMonths = "Jan",
            commissionRate = "15%",
            tuitionFees = "10000",
            website = "www.test.edu",
            whatsappNumber = "+441234567890",
            tags = "Tag1\nTag2",
            notes = "Line 1\nLine 2"
        )

        val file = CsvUtils.exportUniversitiesToCsv(context, listOf(university))

        assertNotNull(file)
        assertTrue(file!!.exists())

        // Because there are newlines in the data, readLines() will read more than 2 lines.
        // It's better to read the whole text and compare.
        val text = file.readText()

        val expectedHeader = "Name,ContactName,Email,Country,City,Status,Priority,PartnershipStatus,IntakeMonths,CommissionRate,TuitionFees,Website,WhatsAppNumber,Tags,Notes\n"
        val expectedData = "\"Test\nUniversity\",\"John\nDoe\",john@test.edu,United Kingdom,London,New,Medium,Prospect,Jan,15%,10000,www.test.edu,+441234567890,\"Tag1\nTag2\",\"Line 1\nLine 2\"\n"

        assertEquals(expectedHeader + expectedData, text)
    }
}
