package com.example.core.ai

data class UkInstitutionTarget(
    val name: String,
    val type: String, // e.g., "UK University", "Pathway Provider", "Language School", "Independent College"
    val country: String = "United Kingdom",
    val officialWebsite: String,
    val internationalOfficeEmail: String,
    val acceptAgentRecruitment: Boolean = true,
    val hasScholarshipInfo: Boolean = true,
    val offerLetterFeeRequired: Boolean = false,
    val partnershipUrl: String = "",
    val LeadScore: Int = 85
)

object UkUniversityResearchEngine {
    private val standardInstitutions = listOf(
        UkInstitutionTarget("University of Greenwich", "UK University", officialWebsite = "https://greenwich.ac.uk", internationalOfficeEmail = "international@greenwich.ac.uk", LeadScore = 92),
        UkInstitutionTarget("University of Hertfordshire", "UK University", officialWebsite = "https://herts.ac.uk", internationalOfficeEmail = "international@herts.ac.uk", LeadScore = 88),
        UkInstitutionTarget("BPP University", "Independent College", officialWebsite = "https://bpp.com", internationalOfficeEmail = "international@bpp.com", LeadScore = 95),
        UkInstitutionTarget("Oxford Brookes University", "UK University", officialWebsite = "https://brookes.ac.uk", internationalOfficeEmail = "international@brookes.ac.uk", LeadScore = 84),
        UkInstitutionTarget("INTO University Partnerships", "Pathway Provider", officialWebsite = "https://intostudy.com", internationalOfficeEmail = "agents@intostudy.com", LeadScore = 90)
    )

    fun getTargetInstitutions(): List<UkInstitutionTarget> = standardInstitutions

    fun calculateLeadScore(institution: UkInstitutionTarget): Int {
        var score = 50
        if (institution.acceptAgentRecruitment) score += 20
        if (institution.hasScholarshipInfo) score += 15
        if (!institution.offerLetterFeeRequired) score += 10
        if (institution.type == "Independent College" || institution.type == "Pathway Provider") score += 5
        return score.coerceAtMost(100)
    }
}
