package com.example.core.data

import com.example.data.TodoItem
import com.example.data.University
import kotlinx.coroutines.flow.Flow

interface IUniversityRepository {
    fun getAllUniversities(): Flow<List<University>>
    fun getActiveUniversities(): Flow<List<University>>
    suspend fun getUniversityById(id: Int): University?
    suspend fun insertUniversity(university: University): Long
    suspend fun updateUniversity(university: University)
    suspend fun deleteUniversity(university: University)
}

interface ITodoRepository {
    fun getAllTodos(): Flow<List<TodoItem>>
    suspend fun insertTodo(todo: TodoItem)
    suspend fun updateTodo(todo: TodoItem)
    suspend fun deleteTodo(todo: TodoItem)
}

object OfflineSyncManager {
    private var isOnline: Boolean = true
    private var lastSyncTimestamp: Long = 0L

    fun setNetworkStatus(online: Boolean) {
        this.isOnline = online
    }

    fun isDeviceOnline(): Boolean = isOnline

    fun markSyncCompleted() {
        lastSyncTimestamp = System.currentTimeMillis()
    }

    fun getLastSyncTime(): Long = lastSyncTimestamp
}
