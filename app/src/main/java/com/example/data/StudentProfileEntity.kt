package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val country: String = "Bangladesh",
    val academicBackground: String = "BSc in Computer Science",
    val sscScore: String = "GPA 5.00",
    val hscScore: String = "GPA 5.00",
    val bachelorDegree: String = "BSc Computer Science & Engineering (CGPA 3.40)",
    val masterDegree: String = "",
    val cgpa: Double = 3.40,
    val gapYears: Int = 1,
    val workExperience: String = "1.5 Years Software Engineer / IT Specialist",
    val preferredIntake: String = "September 2026",
    val preferredSubject: String = "Computer Science / Data Science",
    val preferredCity: String = "London / Manchester / Birmingham",
    val preferredCountry: String = "United Kingdom",
    val budget: Double = 16000.0, // in GBP
    val englishQualification: String = "MOI & IELTS",
    val ieltsOverall: Double = 6.5,
    val moiAvailable: Boolean = true,
    val duolingoScore: Int = 120,
    val pteScore: Int = 62,
    val toeflScore: Int = 85,
    val interviewAvailable: Boolean = true,
    val status: String = "Active Student Profile",
    val createdAt: Long = System.currentTimeMillis()
)
