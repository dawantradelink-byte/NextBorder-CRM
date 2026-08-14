package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_logs")
data class ContactLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val universityId: Int,
    val type: String, // Email, Call, WhatsApp, Meeting
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "university_notes")
data class UniversityNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val universityId: Int,
    val content: String,
    val author: String = "Ishak Dewan",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "meeting_records")
data class MeetingRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val universityId: Int,
    val title: String,
    val meetingDate: Long,
    val meetingLink: String = "",
    val status: String = "Scheduled", // Scheduled, Completed, Cancelled
    val createdAt: Long = System.currentTimeMillis()
)
