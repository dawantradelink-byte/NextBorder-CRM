package com.example.util

import android.util.Patterns

object ValidationUtils {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun cleanTextEncoding(raw: String): String {
        return raw
            .replace("Â£", "£")
            .replace("â€”", "—")
            .replace("â€“", "–")
            .replace("Iâ€™d", "I'd")
            .replace("â€¢", "•")
            .replace("â€™", "'")
            .replace("â€œ", "\"")
            .replace("â€", "\"")
            .replace("Â", "")
            .replace(" ", " ")
    }

    fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isBlank()) return false
        return try {
            if (Patterns.EMAIL_ADDRESS != null) {
                Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
            } else {
                EMAIL_REGEX.matches(trimmed)
            }
        } catch (e: Throwable) {
            EMAIL_REGEX.matches(trimmed)
        }
    }

    fun validateUniversityForm(
        name: String,
        email: String
    ): ValidationResult {
        if (name.trim().isBlank()) {
            return ValidationResult.Error("University Name cannot be empty.")
        }
        if (email.isNotBlank() && !isValidEmail(email)) {
            return ValidationResult.Error("Please enter a valid email address.")
        }
        return ValidationResult.Success
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
