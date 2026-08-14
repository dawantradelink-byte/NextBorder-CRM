package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM student_profiles ORDER BY createdAt DESC")
    fun getAllStudents(): Flow<List<StudentProfileEntity>>

    @Query("SELECT * FROM student_profiles WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Int): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentProfileEntity): Long

    @Update
    suspend fun updateStudent(student: StudentProfileEntity)

    @Delete
    suspend fun deleteStudent(student: StudentProfileEntity)

    @Query("SELECT COUNT(*) FROM student_profiles")
    suspend fun getStudentCount(): Int
}
