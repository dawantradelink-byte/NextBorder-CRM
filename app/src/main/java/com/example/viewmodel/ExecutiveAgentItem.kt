package com.example.viewmodel

data class ExecutiveAgentItem(
    val id: String,
    val name: String,
    val category: String,
    val status: String = "Active", // Active, Running, Idle, Completed
    val completedCount: Int = 12,
    val pendingCount: Int = 2,
    val confidenceScore: Int = 96,
    val roleDescription: String,
    val lastAction: String
)
