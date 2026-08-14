package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "agent_memories")
data class AgentMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val agentName: String,
    val agentType: String,
    val category: String,
    val content: String,
    val impactRating: Int = 5,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "agent_logs")
data class AgentLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val agentName: String,
    val action: String,
    val details: String,
    val status: String = "Success",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "university_replies")
data class UniversityReplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val universityId: Int,
    val universityName: String,
    val subject: String,
    val snippet: String,
    val sentiment: String = "Pending",
    val isRead: Boolean = false,
    val receivedAt: Long = System.currentTimeMillis(),
    val suggestedNextAction: String = ""
)

@Dao
interface AgentDao {
    @Query("SELECT * FROM agent_memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<AgentMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: AgentMemoryEntity)

    @Query("SELECT * FROM agent_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<AgentLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AgentLogEntity)

    @Query("SELECT * FROM university_replies ORDER BY receivedAt DESC")
    fun getAllReplies(): Flow<List<UniversityReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: UniversityReplyEntity)

    @Update
    suspend fun updateReply(reply: UniversityReplyEntity)

    @Query("DELETE FROM agent_logs")
    suspend fun clearLogs()
}
