package com.example.core.i18n

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

enum class SupportedLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    BANGLA("bn", "Bangla", "বাংলা")
}

object LocaleManager {
    private const val PREF_KEY_LANG = "selected_language_code"
    private const val PREFS_NAME = "aios_i18n_prefs"

    fun getSelectedLanguage(context: Context): SupportedLanguage {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(PREF_KEY_LANG, SupportedLanguage.ENGLISH.code) ?: SupportedLanguage.ENGLISH.code
        return SupportedLanguage.entries.find { it.code == code } ?: SupportedLanguage.ENGLISH
    }

    fun setLanguage(context: Context, language: SupportedLanguage): Context {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_KEY_LANG, language.code).apply()
        return updateResources(context, language.code)
    }

    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
