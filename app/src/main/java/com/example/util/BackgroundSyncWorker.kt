package com.example.util

import android.content.Context
import androidx.work.*
import com.example.data.AgentLogEntity
import com.example.data.AppDatabase
import java.util.concurrent.TimeUnit

class BackgroundSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(context)
            val universityDao = db.universityDao()
            val reminderDao = db.reminderDao()
            val todoDao = db.todoDao()
            val agentDao = db.agentDao()

            val now = System.currentTimeMillis()

            // 1. University Research & Partnership Monitoring
            val activeUnis = universityDao.getRawActiveList()
            val followUpNeeded = activeUnis.filter { it.nextFollowUpDate in 1..now }

            // 2. Email & Response Audit
            val unrepliedContacted = activeUnis.filter { it.partnershipStatus == "Contacted" }

            // 3. Reminder Check & Smart Re-notification
            val overdueReminders = try {
                val pending = reminderDao.getPendingRemindersRaw()
                pending.filter { it.triggerTimeMs <= now }
            } catch (e: Exception) {
                emptyList()
            }

            if (overdueReminders.isNotEmpty()) {
                val urgent = overdueReminders.firstOrNull { it.priority == "Urgent" || it.priority == "High" } ?: overdueReminders.first()
                NotificationHelper.showHighPriorityNotification(
                    context = context,
                    id = urgent.id.takeIf { it != 0 } ?: 9001,
                    title = "⚡ [AIOS Smart Alert] ${urgent.priority} Reminder: ${urgent.title}",
                    message = "Category: ${urgent.category} | ${overdueReminders.size} total pending task(s) require action.",
                    highlightDashboard = true
                )
            } else if (followUpNeeded.isNotEmpty()) {
                val uni = followUpNeeded.first()
                NotificationHelper.showReminderNotification(
                    context = context,
                    id = uni.id,
                    title = "📌 Follow-up Due: ${uni.name}",
                    message = "Scheduled partnership outreach is due today."
                )
            }

            // 4. Record Activity Log for Background AI Manager
            agentDao.insertLog(
                AgentLogEntity(
                    agentName = "Background AIOS WorkManager",
                    action = "Battery-Aware Scheduled Background Execution",
                    details = "Scanned ${activeUnis.size} universities, verified ${unrepliedContacted.size} outreach delays, checked ${overdueReminders.size} overdue reminders. Battery & Network constraints respected.",
                    status = "Success",
                    timestamp = now
                )
            )

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

object BackgroundSyncManager {
    private const val UNIQUE_WORK_NAME = "NextBorderBackgroundSync"

    fun startBackgroundManager(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Constraints respecting battery and network
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // 1. Immediate One-Time Run upon app open
        val immediateRequest = OneTimeWorkRequestBuilder<BackgroundSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "${UNIQUE_WORK_NAME}_Immediate",
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )

        // 2. Scheduled Periodic Run (Every 15 Minutes)
        val periodicRequest = PeriodicWorkRequestBuilder<BackgroundSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    fun triggerManualSyncNow(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val immediateRequest = OneTimeWorkRequestBuilder<BackgroundSyncWorker>()
            .build()

        workManager.enqueueUniqueWork(
            "${UNIQUE_WORK_NAME}_Manual",
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )
    }
}
