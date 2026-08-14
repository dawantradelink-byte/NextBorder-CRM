package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExecutiveMemoryDao {
    @Query("SELECT * FROM executive_memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<ExecutiveMemoryEntity>>

    @Query("SELECT * FROM executive_memories WHERE category = :category ORDER BY timestamp DESC")
    fun getMemoriesByCategory(category: String): Flow<List<ExecutiveMemoryEntity>>

    @Query("SELECT * FROM executive_memories WHERE institutionName LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' OR details LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMemories(query: String): Flow<List<ExecutiveMemoryEntity>>

    @Query("SELECT * FROM executive_memories WHERE id = :id LIMIT 1")
    suspend fun getMemoryById(id: Int): ExecutiveMemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: ExecutiveMemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: ExecutiveMemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: ExecutiveMemoryEntity)

    @Query("SELECT COUNT(*) FROM executive_memories")
    suspend fun getMemoryCount(): Int
}
