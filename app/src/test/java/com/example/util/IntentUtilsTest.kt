package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class IntentUtilsTest {

    @Test
    fun launchWhatsApp_whenActivityNotFound_showsToast() {
        val app = ApplicationProvider.getApplicationContext<Context>()
        val exceptionThrowingContext = object : ContextWrapper(app) {
            override fun startActivity(intent: Intent?) {
                throw ActivityNotFoundException()
            }
        }

        IntentUtils.launchWhatsApp(exceptionThrowingContext, "1234567890", "Hello")

        val latestToast = ShadowToast.getTextOfLatestToast()
        assertEquals("WhatsApp application or browser not found", latestToast)
    }

    @Test
    fun launchWhatsApp_whenGenericException_showsToast() {
        val app = ApplicationProvider.getApplicationContext<Context>()
        val exceptionThrowingContext = object : ContextWrapper(app) {
            override fun startActivity(intent: Intent?) {
                throw RuntimeException("Test Exception")
            }
        }

        IntentUtils.launchWhatsApp(exceptionThrowingContext, "1234567890", "Hello")

        val latestToast = ShadowToast.getTextOfLatestToast()
        assertEquals("Could not launch WhatsApp: Test Exception", latestToast)
    }
}
