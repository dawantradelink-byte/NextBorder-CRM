package com.example.core.security

import android.content.Context
import com.example.core.log.AiosLogger
import com.example.core.log.LogCategory

object SecureStorageManager {
    private const val PREFS_SECURE = "aios_secure_prefs"

    fun saveEncryptedString(context: Context, key: String, value: String) {
        val encrypted = SecurityManager.encrypt(context, value)
        val prefs = context.getSharedPreferences(PREFS_SECURE, Context.MODE_PRIVATE)
        prefs.edit().putString(key, encrypted).apply()
        AiosLogger.log(LogCategory.SECURITY, "SecureStorageManager", "Encrypted string saved for key: $key")
    }

    fun getEncryptedString(context: Context, key: String, defaultValue: String = ""): String {
        val prefs = context.getSharedPreferences(PREFS_SECURE, Context.MODE_PRIVATE)
        val encrypted = prefs.getString(key, null) ?: return defaultValue
        return try {
            SecurityManager.decrypt(context, encrypted)
        } catch (e: Exception) {
            AiosLogger.log(LogCategory.SECURITY, "SecureStorageManager", "Failed to decrypt key: $key. Returning default value.")
            defaultValue
        }
    }

    fun removeKey(context: Context, key: String) {
        val prefs = context.getSharedPreferences(PREFS_SECURE, Context.MODE_PRIVATE)
        prefs.edit().remove(key).apply()
    }
}

data class AiosUserSession(
    val userId: String,
    val userName: String,
    val role: String,
    val token: String,
    val isAuthenticated: Boolean
)

object AuthManager {
    private var currentSession: AiosUserSession? = null

    fun initializeSession(user: AiosUserSession) {
        currentSession = user
        AiosLogger.log(LogCategory.SECURITY, "AuthManager", "User session initialized for ${user.userName} [Role: ${user.role}]")
    }

    fun getCurrentSession(): AiosUserSession? = currentSession

    fun isAuthenticated(): Boolean = currentSession?.isAuthenticated == true

    fun logout() {
        AiosLogger.log(LogCategory.SECURITY, "AuthManager", "User logged out.")
        currentSession = null
    }
}
