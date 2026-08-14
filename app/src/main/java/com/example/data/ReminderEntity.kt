package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "Business", // Business, Meeting, Personal, Visa, University, Payment, Custom
    val priority: String = "Medium", // Low, Medium, High, Urgent
    val recurrence: String = "One-time", // One-time, Daily, Weekly, Monthly, Custom
    val customDate: String = "", // e.g. "2026-08-06"
    val customTime: String = "09:00", // e.g. "09:00"
    val triggerTimeMs: Long = System.currentTimeMillis(),
    val repeatIntervalMinutes: Int = 15, // smart retry if ignored
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val universityId: Int? = null,
    val notes: String = "",
    val snoozeCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
