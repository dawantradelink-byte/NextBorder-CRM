package com.example

import com.example.util.ValidationResult
import com.example.util.ValidationUtils
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ValidationUtilsTest {

    @Test
    fun testValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("admissions@university.ac.uk"))
        assertFalse(ValidationUtils.isValidEmail("invalid-email"))
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun testUniversityFormValidation() {
        val resultValid = ValidationUtils.validateUniversityForm("Oxford University", "oxford@ac.uk")
        assertTrue(resultValid is ValidationResult.Success)

        val resultEmptyName = ValidationUtils.validateUniversityForm("", "oxford@ac.uk")
        assertTrue(resultEmptyName is ValidationResult.Error)

        val resultInvalidEmail = ValidationUtils.validateUniversityForm("Oxford", "not_an_email")
        assertTrue(resultInvalidEmail is ValidationResult.Error)
    }

    @Test
    fun testCleanTextEncoding() {
        val messy = "HelloÂ WorldÂ â€“ Next Border"
        val cleaned = ValidationUtils.cleanTextEncoding(messy)
        assertFalse(cleaned.contains("Â"))
        assertTrue(cleaned.contains("Next Border"))
    }
}
