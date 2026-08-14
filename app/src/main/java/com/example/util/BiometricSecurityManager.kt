package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BiometricSecurityManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("next_border_sec_prefs", Context.MODE_PRIVATE)

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private var lastBackgroundTimeMs: Long = 0L

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_LOCK_TIMEOUT_MINS = "lock_timeout_mins"
    }

    var isAppLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_APP_LOCK_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_APP_LOCK_ENABLED, value).apply()
            if (!value) {
                _isLocked.value = false
            }
        }

    var lockTimeoutMinutes: Int
        get() = prefs.getInt(KEY_LOCK_TIMEOUT_MINS, 0) // 0 = Immediately
        set(value) {
            prefs.edit().putInt(KEY_LOCK_TIMEOUT_MINS, value).apply()
        }

    fun canAuthenticate(): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    }

    fun getBiometricStatusDescription(): String {
        return when (canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> "Biometric / Device Lock Available"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No Biometric Hardware Available"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Biometric Hardware Currently Unavailable"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No Biometrics Enrolled (Device PIN / Pattern Fallback Available)"
            else -> "Device Lock Supported"
        }
    }

    fun onAppForegrounded() {
        if (!isAppLockEnabled) {
            _isLocked.value = false
            return
        }

        if (lastBackgroundTimeMs == 0L) {
            _isLocked.value = true
            return
        }

        val elapsedMillis = System.currentTimeMillis() - lastBackgroundTimeMs
        val timeoutMillis = lockTimeoutMinutes * 60 * 1000L

        if (elapsedMillis >= timeoutMillis) {
            _isLocked.value = true
        }
    }

    fun onAppBackgrounded() {
        lastBackgroundTimeMs = System.currentTimeMillis()
    }

    fun forceLock() {
        if (isAppLockEnabled) {
            _isLocked.value = true
        }
    }

    fun setUnlocked() {
        _isLocked.value = false
        lastBackgroundTimeMs = System.currentTimeMillis()
    }

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                setUnlocked()
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Authentication failed. Please try again.")
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Next Border CRM Security")
            .setSubtitle("Unlock to access private university contacts & data")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        try {
            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError("Unlock prompt error: ${e.message}")
        }
    }
}
