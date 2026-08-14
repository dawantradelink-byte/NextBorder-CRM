package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "executive_memories")
data class ExecutiveMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val category: String = "Partnership", // Meeting, Email, University Research, Student Counselling, Follow-up, Partnership, Decision, Daily Summary, Weekly Summary, Monthly Summary
    val institutionName: String = "",
    val details: String = "",
    val status: String = "Completed", // Pending, Completed, In Progress, Critical
    val priority: String = "High", // Urgent, High, Medium, Low
    val firstContactDate: String = "2026-07-01",
    val lastContactDate: String = "2026-08-05",
    val lastEmail: String = "Re: International Student Recruitment & MOI Direct Portal Agreement",
    val meetingNotes: String = "Discussed MOI waiver for Bangladesh applicants & 15% net commission.",
    val scholarshipInfo: String = "Merit Bursary £3,000 - £5,000 Available",
    val englishReq: String = "MOI / IELTS 6.0 / Password Test",
    val moiPolicy: String = "Accepted for medium of instruction within 5 years",
    val interviewPolicy: String = "Free Internal Credibility Interview Offered",
    val agentStatus: String = "Active Partner", // Prospect, Contacted, Active Partner, Agreement Signed
    val priorityScore: Int = 92,
    val riskScore: Int = 10, // 0 to 100
    val timestamp: Long = System.currentTimeMillis()
)
