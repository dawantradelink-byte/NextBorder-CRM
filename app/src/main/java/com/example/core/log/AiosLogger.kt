package com.example.core.log

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogCategory {
    AI_CALL,
    BACKGROUND_WORKER,
    DATABASE,
    NETWORK,
    SECURITY,
    ERROR,
    WARNING,
    PERFORMANCE,
    SYSTEM
}

data class LogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val category: LogCategory,
    val tag: String,
    val message: String,
    val durationMs: Long? = null,
    val errorDetails: String? = null
)

object AiosLogger {
    private val logs = mutableListOf<LogEntry>()
    private const val MAX_LOGS = 1000

    fun log(category: LogCategory, tag: String, message: String, durationMs: Long? = null, errorDetails: String? = null) {
        val entry = LogEntry(
            category = category,
            tag = tag,
            message = message,
            durationMs = durationMs,
            errorDetails = errorDetails
        )
        synchronized(logs) {
            logs.add(entry)
            if (logs.size > MAX_LOGS) {
                logs.removeAt(0)
            }
        }
    }

    fun info(tag: String, message: String) = log(LogCategory.SYSTEM, tag, message)
    fun error(tag: String, message: String, throwable: Throwable? = null) = log(LogCategory.ERROR, tag, message, errorDetails = throwable?.stackTraceToString())
    fun ai(tag: String, message: String, durationMs: Long? = null) = log(LogCategory.AI_CALL, tag, message, durationMs)
    fun worker(tag: String, message: String) = log(LogCategory.BACKGROUND_WORKER, tag, message)
    fun performance(tag: String, message: String, durationMs: Long) = log(LogCategory.PERFORMANCE, tag, message, durationMs)

    fun getLogs(category: LogCategory? = null): List<LogEntry> {
        synchronized(logs) {
            return if (category == null) logs.toList() else logs.filter { it.category == category }
        }
    }

    fun getFormattedLogs(): List<String> {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        synchronized(logs) {
            return logs.map { entry ->
                val time = formatter.format(Date(entry.timestamp))
                "[$time] [${entry.category}] [${entry.tag}] ${entry.message}" +
                        (entry.durationMs?.let { " (${it}ms)" } ?: "") +
                        (entry.errorDetails?.let { " | Error: $it" } ?: "")
            }
        }
    }
}
