package com.example.core.security

import android.content.Context
import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object SecurityManager {
    private const val ALGORITHM = "AES"
    private const val PREFS_NAME = "aios_security_prefs"
    private const val KEY_ALIAS = "aios_master_key"

    fun getOrGenerateKey(context: Context): SecretKey {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encodedKey = prefs.getString(KEY_ALIAS, null)
        return if (encodedKey != null) {
            val decoded = Base64.decode(encodedKey, Base64.DEFAULT)
            SecretKeySpec(decoded, 0, decoded.size, ALGORITHM)
        } else {
            val keyGen = KeyGenerator.getInstance(ALGORITHM)
            keyGen.init(256)
            val key = keyGen.generateKey()
            val encoded = Base64.encodeToString(key.encoded, Base64.DEFAULT)
            prefs.edit().putString(KEY_ALIAS, encoded).apply()
            key
        }
    }

    fun encrypt(context: Context, plainText: String): String {
        if (plainText.isEmpty()) return ""
        val key = getOrGenerateKey(context)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(context: Context, cipherText: String): String {
        if (cipherText.isEmpty()) return ""
        return try {
            val key = getOrGenerateKey(context)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key)
            val decodedBytes = Base64.decode(cipherText, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            plainTextFallback(cipherText)
        }
    }

    private fun plainTextFallback(text: String): String = text
}
