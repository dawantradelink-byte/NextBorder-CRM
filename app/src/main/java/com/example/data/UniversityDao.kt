package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UniversityDao {
    @Query("SELECT * FROM universities WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllActive(): Flow<List<University>>

    @Query("SELECT * FROM universities WHERE isArchived = 0 ORDER BY updatedAt DESC")
    suspend fun getRawActiveList(): List<University>

    @Query("SELECT * FROM universities ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<University>>

    @Query("SELECT * FROM universities WHERE id = :id")
    suspend fun getById(id: Int): University?

    @Query("SELECT * FROM universities WHERE LOWER(name) = LOWER(:name) OR (email != '' AND LOWER(email) = LOWER(:email)) LIMIT 1")
    suspend fun findByNameOrEmail(name: String, email: String): University?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(university: University): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(universities: List<University>)

    @Update
    suspend fun update(university: University)

    @Delete
    suspend fun delete(university: University)

    // Contact Logs
    @Query("SELECT * FROM contact_logs WHERE universityId = :universityId ORDER BY timestamp DESC")
    fun getContactLogs(universityId: Int): Flow<List<ContactLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactLog(log: ContactLog)

    // University Notes
    @Query("SELECT * FROM university_notes WHERE universityId = :universityId ORDER BY createdAt DESC")
    fun getNotes(universityId: Int): Flow<List<UniversityNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: UniversityNote)

    @Delete
    suspend fun deleteNote(note: UniversityNote)

    // Meeting Records
    @Query("SELECT * FROM meeting_records WHERE universityId = :universityId ORDER BY meetingDate ASC")
    fun getMeetings(universityId: Int): Flow<List<MeetingRecord>>

    @Query("SELECT * FROM meeting_records ORDER BY meetingDate ASC")
    fun getAllMeetings(): Flow<List<MeetingRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: MeetingRecord)

    @Delete
    suspend fun deleteMeeting(meeting: MeetingRecord)
}
