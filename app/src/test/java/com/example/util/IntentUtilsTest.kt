package com.example.util

import android.app.Activity
import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class IntentUtilsTest {

    @Test
    fun `launchWebBrowser appends https schema if missing`() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()

        IntentUtils.launchWebBrowser(activity, "www.example.com")

        val shadowActivity = shadowOf(activity)
        val intent = shadowActivity.nextStartedActivity

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("https://www.example.com", intent.data.toString())
    }

    @Test
    fun `launchWebBrowser does not append schema if http is present`() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()

        IntentUtils.launchWebBrowser(activity, "http://www.example.com")

        val shadowActivity = shadowOf(activity)
        val intent = shadowActivity.nextStartedActivity

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("http://www.example.com", intent.data.toString())
    }

    @Test
    fun `launchWebBrowser does not append schema if https is present`() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()

        IntentUtils.launchWebBrowser(activity, "https://www.example.com")

        val shadowActivity = shadowOf(activity)
        val intent = shadowActivity.nextStartedActivity

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("https://www.example.com", intent.data.toString())
    }
}
