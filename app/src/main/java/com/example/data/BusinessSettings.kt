package com.example.data

import android.content.Context
import android.content.SharedPreferences

data class BusinessSettings(
    val agencyName: String = "Next Border",
    val ceoName: String = "Ishak Dewan",
    val officialEmail: String = "nextborder.visa@gmail.com",
    val whatsappNumber: String = "+44 7700 900077",
    val emailSignature: String = "Ishak Dewan\nCEO, Next Border Visa Consultancy\nEmail: nextborder.visa@gmail.com",
    val defaultTone: String = "Persuasive",
    val defaultCommissionModel: String = "Strictly commission-based B2B model (no student extra fees)",
    val website: String = "https://nextborder.co.uk",
    val isDarkTheme: Boolean = true
)

class BusinessSettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("business_settings", Context.MODE_PRIVATE)

    fun getSettings(): BusinessSettings {
        return BusinessSettings(
            agencyName = prefs.getString("agency_name", "Next Border") ?: "Next Border",
            ceoName = prefs.getString("ceo_name", "Ishak Dewan") ?: "Ishak Dewan",
            officialEmail = prefs.getString("official_email", "nextborder.visa@gmail.com") ?: "nextborder.visa@gmail.com",
            whatsappNumber = prefs.getString("whatsapp", "+44 7700 900077") ?: "+44 7700 900077",
            emailSignature = prefs.getString("email_signature", "Ishak Dewan\nCEO, Next Border Visa Consultancy\nEmail: nextborder.visa@gmail.com") ?: "Ishak Dewan\nCEO, Next Border Visa Consultancy\nEmail: nextborder.visa@gmail.com",
            defaultTone = prefs.getString("default_tone", "Persuasive") ?: "Persuasive",
            defaultCommissionModel = prefs.getString("default_commission", "Strictly commission-based B2B model (no student extra fees)") ?: "Strictly commission-based B2B model (no student extra fees)",
            website = prefs.getString("website", "https://nextborder.co.uk") ?: "https://nextborder.co.uk",
            isDarkTheme = prefs.getBoolean("is_dark_theme", true)
        )
    }

    fun saveSettings(settings: BusinessSettings) {
        prefs.edit()
            .putString("agency_name", settings.agencyName)
            .putString("ceo_name", settings.ceoName)
            .putString("official_email", settings.officialEmail)
            .putString("whatsapp", settings.whatsappNumber)
            .putString("email_signature", settings.emailSignature)
            .putString("default_tone", settings.defaultTone)
            .putString("default_commission", settings.defaultCommissionModel)
            .putString("website", settings.website)
            .putBoolean("is_dark_theme", settings.isDarkTheme)
            .apply()
    }
}
