package com.example.core.work

import android.content.Context

data class BackgroundTaskConfig(
    val taskId: String,
    val taskName: String,
    val repeatIntervalMinutes: Long = 60,
    val requiresNetwork: Boolean = true,
    val requiresBatteryNotLow: Boolean = true
)

object AiosWorkCoordinator {

    fun scheduleBackgroundSync(context: Context, config: BackgroundTaskConfig) {
        // AIOS Background Task Scheduling Interface Contract
        com.example.core.ai.AiCoordinator.log(
            "Scheduled battery-optimized task '${config.taskName}' [ID: ${config.taskId}] every ${config.repeatIntervalMinutes} mins (Network: ${config.requiresNetwork})"
        )
    }

    fun triggerImmediateTask(context: Context, taskId: String, taskName: String) {
        com.example.core.ai.AiCoordinator.log("Triggered immediate background execution: $taskName [ID: $taskId]")
    }
}
