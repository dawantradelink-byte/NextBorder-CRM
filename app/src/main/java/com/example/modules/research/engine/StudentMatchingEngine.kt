package com.example.modules.research.engine

import com.example.data.StudentProfileEntity
import com.example.modules.research.model.ResearchedInstitutionEntity

data class StudentMatchResult(
    val student: StudentProfileEntity,
    val institution: ResearchedInstitutionEntity,
    val matchScore: Int, // 0 to 100
    val suggestedCourse: String,
    val estimatedTuitionFee: String,
    val scholarshipAvailable: Boolean,
    val scholarshipDetails: String,
    val moiAccepted: Boolean,
    val interviewAccepted: Boolean,
    val minEnglishReq: String,
    val applicationFee: String,
    val applicationDeadline: String,
    val ceoInsights: String,
    val visaSuccessPotential: String = "High (92%+ CAS Approval Rate)"
)

object StudentMatchingEngine {

    fun matchStudentWithInstitutions(
        student: StudentProfileEntity,
        institutions: List<ResearchedInstitutionEntity>
    ): List<StudentMatchResult> {
        return institutions.map { inst ->
            calculateMatch(student, inst)
        }.sortedByDescending { it.matchScore }
    }

    private fun calculateMatch(
        student: StudentProfileEntity,
        inst: ResearchedInstitutionEntity
    ): StudentMatchResult {
        var score = 40 // Base score

        val insights = mutableListOf<String>()

        // 1. English Requirement Compatibility
        if (student.moiAvailable && inst.moiAccepted) {
            score += 20
            insights.add("High potential candidate for MOI admission waiver without mandatory IELTS.")
        } else if (student.ieltsOverall >= inst.minIeltsScore) {
            score += 18
            insights.add("IELTS score (${student.ieltsOverall}) satisfies minimum entry requirement (${inst.minIeltsScore}).")
        } else if (student.interviewAvailable && inst.internalInterviewOffered) {
            score += 15
            insights.add("Eligible for free University Internal English Test / Interview option.")
        }

        // 2. Academic Fit & CGPA
        if (student.cgpa >= 3.0) {
            score += 15
            insights.add("Strong academic background (CGPA ${student.cgpa}) fits postgraduate entry.")
        } else if (student.cgpa >= 2.5) {
            score += 10
            insights.add("Acceptable academic record; pre-sessional or standard entry route recommended.")
        }

        // 3. Budget & Scholarship Opportunity
        if (inst.scholarshipAvailable || inst.scholarshipDetails.isNotBlank()) {
            score += 12
            insights.add("Excellent candidate for scholarship search (${inst.scholarshipDetails}).")
        }

        // 4. Admission Flexibility & Fast Turnaround
        if (inst.averageProcessingTime.contains("Day", ignoreCase = true)) {
            score += 8
            insights.add("Fast application processing time (${inst.averageProcessingTime}). Recommend applying within the next 14 days.")
        }

        // 5. Gap Year Flexibility
        if (student.gapYears <= 2) {
            score += 5
        } else {
            insights.add("Check 28-day financial proof and statement of purpose for ${student.gapYears} gap years.")
        }

        val finalScore = score.coerceIn(10, 99)

        val courseName = when {
            student.preferredSubject.contains("Computer", ignoreCase = true) || student.preferredSubject.contains("IT", ignoreCase = true) ->
                "MSc Advanced Computer Science / MSc Data Science & AI"
            student.preferredSubject.contains("Business", ignoreCase = true) || student.preferredSubject.contains("Management", ignoreCase = true) ->
                "MSc International Business Management / MBA"
            else -> "MSc ${student.preferredSubject} (September Intake)"
        }

        val estimatedFee = if (inst.tierLevel.startsWith("Tier 3") || inst.tierLevel.startsWith("Tier 2")) "£13,500 - £14,500 Net" else "£15,500 - £17,000 Net"
        val appDeadline = "15 August 2026 for September Intake"

        val combinedInsight = if (insights.isNotEmpty()) {
            insights.joinToString(" • ")
        } else {
            "Suitable profile for direct application. Recommend submitting documents within 14 days."
        }

        return StudentMatchResult(
            student = student,
            institution = inst,
            matchScore = finalScore,
            suggestedCourse = courseName,
            estimatedTuitionFee = estimatedFee,
            scholarshipAvailable = inst.scholarshipAvailable || inst.scholarshipDetails.isNotBlank(),
            scholarshipDetails = if (inst.scholarshipDetails.isNotBlank()) inst.scholarshipDetails else "Standard Merit Bursary Available",
            moiAccepted = inst.moiAccepted,
            interviewAccepted = inst.internalInterviewOffered,
            minEnglishReq = "IELTS ${inst.minIeltsScore} / ${inst.englishWaiverPolicy}",
            applicationFee = inst.applicationFeeInfo,
            applicationDeadline = appDeadline,
            ceoInsights = combinedInsight
        )
    }
}
