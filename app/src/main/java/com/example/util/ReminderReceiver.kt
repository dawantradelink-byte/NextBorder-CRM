package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "CRM University Follow-up Reminder"
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "You have a scheduled university follow-up task pending."
        val notificationId = intent.getIntExtra("EXTRA_ID", System.currentTimeMillis().toInt())

        NotificationHelper.showReminderNotification(context, notificationId, title, message)
    }
}
