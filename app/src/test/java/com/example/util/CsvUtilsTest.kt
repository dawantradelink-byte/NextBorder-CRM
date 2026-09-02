package com.example.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.University
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class CsvUtilsTest {

    private lateinit var context: Context
    private lateinit var exportFile: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        exportFile = File(context.cacheDir, "next_border_universities_export.csv")

        // Ensure cleanup before test
        if (exportFile.exists()) {
            exportFile.delete()
        }
    }

    @After
    fun tearDown() {
        // Ensure cleanup after test
        if (exportFile.exists()) {
            exportFile.delete()
        }
    }

    @Test
    fun `exportUniversitiesToCsv returns null on exception`() {
        // Create a directory with the exact name of the file that CsvUtils will try to create.
        // This will cause FileOutputStream(file) to throw a FileNotFoundException (Is a directory).
        exportFile.mkdirs()

        val universities = listOf(
            University(name = "Test Uni", contactName = "Contact", email = "test@uni.edu")
        )

        val result = CsvUtils.exportUniversitiesToCsv(context, universities)
        assertNull("Expected null due to FileOutputStream exception", result)
    }

    @Test
    fun `exportUniversitiesToCsv returns file on success`() {
        val universities = listOf(
            University(name = "Test Uni", contactName = "Contact", email = "test@uni.edu")
        )

        val result = CsvUtils.exportUniversitiesToCsv(context, universities)
        assertNotNull("Expected valid file to be created", result)
        assertTrue("Created file should exist", result!!.exists())
        assertTrue("Created file should not be empty", result.length() > 0)

        // Read first few lines to verify content (Header + Data)
        val lines = result.readLines()
        assertEquals(2, lines.size)
        assertTrue(lines[0].startsWith("Name,ContactName,Email"))
        assertTrue(lines[1].contains("Test Uni"))
    }
}
