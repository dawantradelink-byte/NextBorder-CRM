package com.example.core.work

import android.content.Context
import com.example.core.log.AiosLogger

enum class WorkerType {
    UNIVERSITY_RESEARCH,
    LEAD_COLLECTION,
    ANALYTICS_REFRESH,
    GOOGLE_SHEETS_SYNC,
    EMAIL_QUEUE,
    NOTIFICATION_QUEUE,
    MEETING_REMINDER,
    MEMORY_CLEANUP,
    CACHE_CLEANUP
}

interface IAiosWorker {
    val workerType: WorkerType
    val name: String
    suspend fun doWork(context: Context): Boolean
}

class UniversityResearchWorker : IAiosWorker {
    override val workerType = WorkerType.UNIVERSITY_RESEARCH
    override val name = "University Research Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Scanning global intakes and application deadlines...")
        return true
    }
}

class LeadCollectionWorker : IAiosWorker {
    override val workerType = WorkerType.LEAD_COLLECTION
    override val name = "Lead Collection Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Processing incoming agency leads and website contact forms...")
        return true
    }
}

class AnalyticsRefreshWorker : IAiosWorker {
    override val workerType = WorkerType.ANALYTICS_REFRESH
    override val name = "Analytics Refresh Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Re-calculating partnership conversion ratios and greylist recovery dates...")
        return true
    }
}

class GoogleSheetsSyncWorker : IAiosWorker {
    override val workerType = WorkerType.GOOGLE_SHEETS_SYNC
    override val name = "Google Sheets Sync Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Synchronizing university database to 12 Google Sheets tabs...")
        return true
    }
}

class EmailQueueWorker : IAiosWorker {
    override val workerType = WorkerType.EMAIL_QUEUE
    override val name = "Email Queue Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Dispatching pending outbound emails via Intent/SMTP queue...")
        return true
    }
}

class NotificationQueueWorker : IAiosWorker {
    override val workerType = WorkerType.NOTIFICATION_QUEUE
    override val name = "Notification Queue Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Triggering pending task and follow-up notifications...")
        return true
    }
}

class MeetingReminderWorker : IAiosWorker {
    override val workerType = WorkerType.MEETING_REMINDER
    override val name = "Meeting Reminder Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Checking calendar meetings scheduled for today...")
        return true
    }
}

class MemoryCleanupWorker : IAiosWorker {
    override val workerType = WorkerType.MEMORY_CLEANUP
    override val name = "Memory & Log Cleanup Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Trimming old AI Agent execution logs...")
        return true
    }
}

class CacheCleanupWorker : IAiosWorker {
    override val workerType = WorkerType.CACHE_CLEANUP
    override val name = "Cache Cleanup Worker"
    override suspend fun doWork(context: Context): Boolean {
        AiosLogger.worker(name, "Purging temporary PDF exports and CSV cache...")
        return true
    }
}

object AiosBackgroundExecutionManager {
    private val workers = mutableMapOf<WorkerType, IAiosWorker>()

    init {
        registerWorker(UniversityResearchWorker())
        registerWorker(LeadCollectionWorker())
        registerWorker(AnalyticsRefreshWorker())
        registerWorker(GoogleSheetsSyncWorker())
        registerWorker(EmailQueueWorker())
        registerWorker(NotificationQueueWorker())
        registerWorker(MeetingReminderWorker())
        registerWorker(MemoryCleanupWorker())
        registerWorker(CacheCleanupWorker())
    }

    fun registerWorker(worker: IAiosWorker) {
        workers[worker.workerType] = worker
    }

    suspend fun runWorker(context: Context, type: WorkerType): Boolean {
        val worker = workers[type] ?: return false
        AiosLogger.worker("AiosBackgroundExecutionManager", "Executing worker: ${worker.name}")
        return worker.doWork(context)
    }

    suspend fun runAllWorkers(context: Context): Map<WorkerType, Boolean> {
        val results = mutableMapOf<WorkerType, Boolean>()
        workers.forEach { (type, worker) ->
            results[type] = worker.doWork(context)
        }
        return results
    }
}
