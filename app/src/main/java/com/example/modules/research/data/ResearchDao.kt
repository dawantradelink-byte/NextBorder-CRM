package com.example.modules.research.data

import androidx.room.*
import com.example.modules.research.model.ResearchJobEntity
import com.example.modules.research.model.ResearchedInstitutionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResearchDao {
    @Query("SELECT * FROM researched_institutions ORDER BY lastUpdated DESC")
    fun getAllInstitutions(): Flow<List<ResearchedInstitutionEntity>>

    @Query("SELECT * FROM researched_institutions ORDER BY lastUpdated DESC")
    suspend fun getRawInstitutionsList(): List<ResearchedInstitutionEntity>

    @Query("SELECT * FROM researched_institutions WHERE LOWER(institutionName) = LOWER(:name) OR LOWER(officialWebsite) = LOWER(:website) LIMIT 1")
    suspend fun findByNameOrWebsite(name: String, website: String): ResearchedInstitutionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstitution(institution: ResearchedInstitutionEntity): Long

    @Update
    suspend fun updateInstitution(institution: ResearchedInstitutionEntity)

    @Update
    suspend fun updateAllInstitutions(institutions: List<ResearchedInstitutionEntity>)

    @Query("SELECT * FROM research_jobs ORDER BY assignedAt DESC")
    fun getAllJobs(): Flow<List<ResearchJobEntity>>

    @Query("SELECT * FROM research_jobs ORDER BY assignedAt DESC")
    suspend fun getRawJobsList(): List<ResearchJobEntity>

    @Query("SELECT * FROM research_jobs WHERE status = 'Pending' ORDER BY assignedAt ASC")
    suspend fun getPendingJobs(): List<ResearchJobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: ResearchJobEntity): Long

    @Update
    suspend fun updateJob(job: ResearchJobEntity)

    @Query("DELETE FROM researched_institutions")
    suspend fun clearInstitutions()

    @Query("DELETE FROM research_jobs")
    suspend fun clearJobs()
}
