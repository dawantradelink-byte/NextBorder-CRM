package com.example.util

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReminderSchedulerTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var shadowAlarmManager: ShadowAlarmManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = shadowOf(alarmManager)
    }

    @Test
    fun `scheduleReminder should set exact alarm and contain correct intent extras`() {
        val reminderId = 123
        val title = "Stanford University"
        val description = "Discuss admission requirements"
        val triggerTimeMs = 1000L

        ReminderScheduler.scheduleReminder(
            context,
            reminderId,
            title,
            description,
            triggerTimeMs
        )

        val scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assertEquals(1, scheduledAlarms.size)

        val alarm = scheduledAlarms[0]
        assertEquals(AlarmManager.RTC_WAKEUP, alarm.type)
        assertEquals(triggerTimeMs, alarm.triggerAtTime)

        val pendingIntent = alarm.operation
        val shadowPendingIntent = shadowOf(pendingIntent)

        val intent = shadowPendingIntent.savedIntent
        assertEquals(ReminderReceiver::class.java.name, intent.component?.className)
        assertEquals(reminderId, intent.getIntExtra("EXTRA_ID", -1))
        assertEquals("Follow-up Reminder: Stanford University", intent.getStringExtra("EXTRA_TITLE"))
        assertEquals("Discuss admission requirements", intent.getStringExtra("EXTRA_MESSAGE"))
    }

    @Test
    fun `scheduleReminder with blank note should use default message`() {
        val reminderId = 456
        val title = "MIT"
        val triggerTimeMs = 2000L

        ReminderScheduler.scheduleReminder(
            context,
            reminderId,
            title,
            "   ",
            triggerTimeMs
        )

        val scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assertEquals(1, scheduledAlarms.size)

        val alarm = scheduledAlarms[0]
        val intent = shadowOf(alarm.operation).savedIntent

        assertEquals("Scheduled follow-up reminder for MIT", intent.getStringExtra("EXTRA_MESSAGE"))
    }

    @Test
    fun `cancelReminder should cancel the pending intent`() {
        val reminderId = 789

        ReminderScheduler.scheduleReminder(
            context,
            reminderId,
            "Harvard",
            "Note",
            3000L
        )

        assertEquals(1, shadowAlarmManager.scheduledAlarms.size)

        ReminderScheduler.cancelReminder(context, reminderId)

        assertEquals(0, shadowAlarmManager.scheduledAlarms.size)
    }
}
