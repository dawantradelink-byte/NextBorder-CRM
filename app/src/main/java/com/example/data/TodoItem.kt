package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val task: String,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val priority: String = "Medium", // High, Medium, Low
    val universityId: Int? = null,
    val completedAt: Long? = null,
    val recurringRule: String? = null, // Daily, Weekly, Monthly
    val createdAt: Long = System.currentTimeMillis()
)
