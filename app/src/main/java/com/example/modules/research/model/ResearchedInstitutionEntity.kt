package com.example.modules.research.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "researched_institutions")
data class ResearchedInstitutionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val institutionName: String,
    val officialWebsite: String = "",
    val country: String = "United Kingdom",
    val city: String = "",
    val institutionType: String = "University", // University, College, Pathway Provider, Institute
    val internationalOfficeContact: String = "international@institution.ac.uk",
    val admissionsOfficeContact: String = "admissions@institution.ac.uk",
    val publicContactEmail: String = "info@institution.ac.uk",
    val publicPhone: String = "+44 (0)20 7946 0991",
    val agentRecruitmentAvailable: Boolean = true,
    val scholarshipAvailable: Boolean = true,
    val scholarshipDetails: String = "International Vice-Chancellor Merit Scholarship (Up to £5,000)",
    val applicationFeeInfo: String = "Free / Waiver Available",
    val feeWaiverAvailable: Boolean = true,
    val commissionInfo: String = "Standard UK Agent Agreement (15% Net Tuition)",
    val entryRequirements: String = "MOI Accepted for English Proficiency, 55% Bachelor Threshold",
    val rankingInfo: String = "Top 50 UK University (QS World Ranking)",
    val latestNews: String = "September Intake Open for South Asian Applicants with MOI",
    val rssFeedUrl: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val confidenceScore: Double = 0.95,
    val researchSource: String = "Official University Web Portal & Public International Office",
    val isSyncedToMainCrm: Boolean = false,

    // Phase 13 Practical University Intelligence & Multi-Tier Recruitment
    val tierLevel: String = "Tier 1: Mid-ranked UK University",
    val acceptedEnglishTests: String = "IELTS, PTE, Duolingo, MOI, Internal English Interview, Password Test",
    val moiAccepted: Boolean = true,
    val internalInterviewOffered: Boolean = true,
    val englishWaiverPolicy: String = "MOI Accepted within 5 years of graduation",
    val minIeltsScore: Double = 6.0,
    val depositRequirement: String = "£3,000 CAS Deposit",
    val casProcessOverview: String = "Pre-CAS interview required, Bank balance 28-day check",
    val averageProcessingTime: String = "3-5 Working Days",
    val opportunityScore: Int = 90,
    val recruitmentSuitability: String = "High - Flexible Admissions & Direct Agent Portal",
    val partnershipPotential: String = "High B2B Opportunity",
    val scholarshipPotential: String = "High (£3,000 - £5,000 Bursary)",
    val englishFlexibility: String = "Very Flexible (MOI + Password Test + Interview)"
)
