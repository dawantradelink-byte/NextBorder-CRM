package com.example.modules.research.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "research_jobs")
data class ResearchJobEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val agentType: String, // e.g. "UK University Agent", "Scholarship Agent", etc.
    val targetScope: String, // Target search scope or institution name
    val status: String = "Pending", // Pending, In Progress, Completed, Failed
    val resultsFound: Int = 0,
    val duplicatesPrevented: Int = 0,
    val details: String = "",
    val assignedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
