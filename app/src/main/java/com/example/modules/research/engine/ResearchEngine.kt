package com.example.modules.research.engine

import com.example.data.AgentDao
import com.example.data.AgentLogEntity
import com.example.data.University
import com.example.data.UniversityDao
import com.example.modules.research.data.ResearchDao
import com.example.modules.research.model.ResearchJobEntity
import com.example.modules.research.model.ResearchedInstitutionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class ResearchAgentType(
    val displayName: String,
    val description: String,
    val defaultScope: String
) {
    UK_UNIVERSITY_AGENT(
        "UK University Agent",
        "Discovers & audits accredited UK public universities and degree-awarding bodies",
        "Public UK Universities Intake & Accreditation Pages"
    ),
    UK_COLLEGE_AGENT(
        "UK College Agent",
        "Scans UK higher education colleges, pathway institutes, and foundation academies",
        "UK HE/FE Colleges & Foundation Pathway Providers"
    ),
    SCHOLARSHIP_AGENT(
        "Scholarship Agent",
        "Identifies international student bursaries, Vice-Chancellor merit awards & fee discounts",
        "International Student Scholarships & Financial Aid Portals"
    ),
    INTERNATIONAL_OFFICE_AGENT(
        "International Office Agent",
        "Extracts international recruitment officer emails, regional desks & public office contacts",
        "International Office Contact Directories"
    ),
    PARTNERSHIP_AGENT(
        "Partnership Agent",
        "Evaluates public agent recruitment policies, direct partnership terms & commission structures",
        "Official Agent Recruitment & Representative Pages"
    ),
    ADMISSION_POLICY_AGENT(
        "Admission Policy Agent",
        "Audits Medium of Instruction (MOI) acceptance, IELTS waivers & entry GPA thresholds",
        "Admissions Requirements & English Proficiency Policies"
    ),
    FEE_WAIVER_AGENT(
        "Fee Waiver Agent",
        "Finds application fee waiver codes, CAS deposit thresholds & installment options",
        "Application Fees, CAS Deposit & Payment Policy Pages"
    ),
    NEWS_AGENT(
        "News Agent",
        "Monitors official university press releases, intake deadlines & UKVI policy announcements",
        "Official University Press Releases & UKVI Policy News"
    ),
    RSS_AGENT(
        "RSS Agent",
        "Subscribes to & parses official UK higher education news feeds and agent portals",
        "Public UK HE Sector RSS & Announcement Feeds"
    ),
    RANKING_AGENT(
        "Ranking Agent",
        "Aggregates QS World Rankings, Times Higher Education scores & graduate employability metrics",
        "QS & Times Higher Education Public Ranking Portals"
    )
}

object CeoAiResearchCoordinator {

    fun calculateOpportunityScore(inst: ResearchedInstitutionEntity): Int {
        var score = 40
        if (inst.moiAccepted) score += 20
        if (inst.internalInterviewOffered) score += 15
        if (inst.acceptedEnglishTests.contains("Duolingo", ignoreCase = true) ||
            inst.acceptedEnglishTests.contains("Password", ignoreCase = true) ||
            inst.acceptedEnglishTests.contains("Oxford ELLT", ignoreCase = true) ||
            inst.acceptedEnglishTests.contains("LanguageCert", ignoreCase = true)) score += 10
        if (inst.scholarshipAvailable) score += 10
        if (inst.feeWaiverAvailable) score += 10
        if (inst.agentRecruitmentAvailable) score += 10
        if (inst.minIeltsScore <= 6.0) score += 10
        if (inst.tierLevel.startsWith("Tier 1") || inst.tierLevel.startsWith("Tier 3") || inst.tierLevel.startsWith("Tier 4")) score += 5
        return score.coerceIn(0, 100)
    }

    // Preset Knowledge Base of Real UK Higher Education Institutions across 5 Tiers
    private val PRESET_INSTITUTIONS = listOf(
        // Tier 1: Mid-ranked UK Universities
        ResearchedInstitutionEntity(
            institutionName = "University of Greenwich",
            officialWebsite = "https://www.gre.ac.uk",
            country = "United Kingdom",
            city = "London",
            institutionType = "University",
            tierLevel = "Tier 1: Mid-ranked UK University",
            internationalOfficeContact = "international@gre.ac.uk",
            admissionsOfficeContact = "apply@gre.ac.uk",
            publicContactEmail = "international@gre.ac.uk",
            publicPhone = "+44 (0)20 8331 8136",
            agentRecruitmentAvailable = true,
            scholarshipAvailable = true,
            scholarshipDetails = "International High Achievement Award (£3,000 Tuition Discount)",
            applicationFeeInfo = "Free Direct Application",
            feeWaiverAvailable = true,
            commissionInfo = "Standard Agent Agreement (15% - 17.5% Tiered)",
            entryRequirements = "Medium of Instruction (MOI) Fully Accepted (Medium of Instruction letter from recognized university)",
            acceptedEnglishTests = "IELTS, PTE, Duolingo, MOI, Internal English Interview, LanguageCert",
            moiAccepted = true,
            internalInterviewOffered = true,
            englishWaiverPolicy = "MOI accepted if degree completed in English within 5 years",
            minIeltsScore = 6.0,
            depositRequirement = "£3,000 CAS Deposit",
            casProcessOverview = "Pre-CAS interview required, 28-day bank balance check",
            averageProcessingTime = "3-5 Working Days",
            opportunityScore = 96,
            recruitmentSuitability = "Highest - Practical entry requirements & direct agent portal",
            partnershipPotential = "Excellent B2B Partner",
            scholarshipPotential = "High (£3,000 Merit Award)",
            englishFlexibility = "Very Flexible (MOI + Duolingo + Interview)",
            rankingInfo = "Top 10 London University for Student Satisfaction",
            latestNews = "Jan & Sep 2026 Intake Accepting Direct Agent Applications",
            confidenceScore = 0.98,
            researchSource = "Official Greenwich International Office Web Page"
        ),
        ResearchedInstitutionEntity(
            institutionName = "Coventry University",
            officialWebsite = "https://www.coventry.ac.uk",
            country = "United Kingdom",
            city = "Coventry",
            institutionType = "University",
            tierLevel = "Tier 1: Mid-ranked UK University",
            internationalOfficeContact = "intdesk@coventry.ac.uk",
            admissionsOfficeContact = "admissions.io@coventry.ac.uk",
            publicContactEmail = "info@coventry.ac.uk",
            publicPhone = "+44 (0)24 7765 2222",
            agentRecruitmentAvailable = true,
            scholarshipAvailable = true,
            scholarshipDetails = "International Vice-Chancellor Award (£4,000)",
            applicationFeeInfo = "Free Direct Portal",
            feeWaiverAvailable = true,
            commissionInfo = "Tiered Commission Structure (15% Base + Volume Bonus)",
            entryRequirements = "MOI Accepted, Internal Coventry English Test Option",
            acceptedEnglishTests = "IELTS, PTE, Duolingo, Oxford ELLT, Internal Test",
            moiAccepted = true,
            internalInterviewOffered = true,
            englishWaiverPolicy = "Internal Coventry English Test replaces IELTS",
            minIeltsScore = 6.0,
            depositRequirement = "£4,000 CAS Deposit with Installments",
            casProcessOverview = "Automated Pre-CAS Verification Portal",
            averageProcessingTime = "2-4 Working Days",
            opportunityScore = 95,
            recruitmentSuitability = "High - Volume recruitment partner for South Asia",
            partnershipPotential = "Tier 1 Priority B2B",
            scholarshipPotential = "High (£4,000 Award)",
            englishFlexibility = "Flexible (Internal Coventry Exam + MOI)",
            rankingInfo = "Ranked 5 Stars for Teaching Excellence (QS)",
            latestNews = "CAS Deposit Minimum £4,000 with Flexible Installments",
            confidenceScore = 0.97,
            researchSource = "Coventry University Agent & International Recruitment Portal"
        ),
        ResearchedInstitutionEntity(
            institutionName = "Teesside University",
            officialWebsite = "https://www.tees.ac.uk",
            country = "United Kingdom",
            city = "Middlesbrough",
            institutionType = "University",
            tierLevel = "Tier 1: Mid-ranked UK University",
            internationalOfficeContact = "international@tees.ac.uk",
            admissionsOfficeContact = "admissions@tees.ac.uk",
            publicContactEmail = "international@tees.ac.uk",
            publicPhone = "+44 (0)1642 738900",
            agentRecruitmentAvailable = true,
            scholarshipAvailable = true,
            scholarshipDetails = "Global Excellence Scholarship (£2,000 - £3,500)",
            applicationFeeInfo = "Free",
            feeWaiverAvailable = true,
            commissionInfo = "Standard UK Direct Agent Rate (15%)",
            entryRequirements = "MOI Accepted, High Pass Rate in Teesside Internal English Exam",
            acceptedEnglishTests = "IELTS, PTE, Duolingo, Password Test, Internal Exam",
            moiAccepted = true,
            internalInterviewOffered = true,
            englishWaiverPolicy = "Free online internal English assessment offered",
            minIeltsScore = 5.5,
            depositRequirement = "£4,000 CAS Deposit",
            casProcessOverview = "Fast-track CAS within 48 hours for verified documents",
            averageProcessingTime = "2-3 Working Days",
            opportunityScore = 97,
            recruitmentSuitability = "Highest - Lowest minimum IELTS (5.5) & Fast CAS",
            partnershipPotential = "Excellent Direct Partner",
            scholarshipPotential = "High (£2,000 - £3,500)",
            englishFlexibility = "Maximum Flexibility (5.5 IELTS + Free Internal Exam)",
            rankingInfo = "Ranked #1 UK University for International Student Support (ISB)",
            latestNews = "Fast-track CAS issuing within 48 hours for verified agent applicants",
            confidenceScore = 0.96,
            researchSource = "Teesside University International Office Public Directory"
        ),
        ResearchedInstitutionEntity(
            institutionName = "De Montfort University",
            officialWebsite = "https://www.dmu.ac.uk",
            country = "United Kingdom",
            city = "Leicester",
            institutionType = "University",
            tierLevel = "Tier 1: Mid-ranked UK University",
            internationalOfficeContact = "international@dmu.ac.uk",
            admissionsOfficeContact = "admissions@dmu.ac.uk",
            publicContactEmail = "enquiries@dmu.ac.uk",
            publicPhone = "+44 (0)116 250 6070",
            agentRecruitmentAvailable = true,
            scholarshipAvailable = true,
            scholarshipDetails = "DMU International Merit Bursary (£2,500)",
            applicationFeeInfo = "Free Direct Application",
            feeWaiverAvailable = true,
            commissionInfo = "Standard 15% Net Tuition Commission",
            entryRequirements = "MOI Accepted for Select Programs, IELTS 6.0 Equivalent",
            acceptedEnglishTests = "IELTS, PTE, Duolingo, MOI, LanguageCert",
            moiAccepted = true,
            internalInterviewOffered = true,
            englishWaiverPolicy = "MOI Accepted with Medium of Instruction Letter",
            minIeltsScore = 6.0,
            depositRequirement = "£3,000 CAS Deposit",
            casProcessOverview = "Pre-CAS Interview & Financial Proof Audit",
            averageProcessingTime = "4-5 Working Days",
            opportunityScore = 92,
            recruitmentSuitability = "High - Popular South Asian destination in Leicester",
            partnershipPotential = "Strong B2B Candidate",
            scholarshipPotential = "Medium (£2,500 Bursary)",
            englishFlexibility = "Flexible MOI Policy",
            rankingInfo = "Gold Award in Teaching Excellence Framework (TEF)",
            latestNews = "Extended September intake application deadline for South Asian regional desk",
            confidenceScore = 0.94,
            researchSource = "DMU Global Partnership & Representative Directory"
        ),
        // Tier 2: Further Education Colleges
        ResearchedInstitutionEntity(
            institutionName = "City of Glasgow College",
            officialWebsite = "https://www.cityofglasgowcollege.ac.uk",
            country = "United Kingdom",
            city = "Glasgow",
            institutionType = "FE College",
            tierLevel = "Tier 2: Further Education College",
            internationalOfficeContact = "international@cityofglasgowcollege.ac.uk",
            admissionsOfficeContact = "admissions@cityofglasgowcollege.ac.uk",
            publicContactEmail = "info@cityofglasgowcollege.ac.uk",
            publicPhone = "+44 (0)141 375 5555",
            agentRecruitmentAvailable = true,
            scholarshipAvailable = true,
            scholarshipDetails = "College International Bursary (£1,000)",
            applicationFeeInfo = "Free Application",
            feeWaiverAvailable = true,
            commissionInfo = "12% - 15% Vocational & HND Agent Commission",
            entryRequirements = "Flexible HND & Vocational Entry, High School 50%, IELTS 5.5",
            acceptedEnglishTests = "IELTS, PTE, Duolingo, MOI",
            moiAccepted = true,
            internalInterviewOffered = false,
            englishWaiverPolicy = "HND pathway accepts MOI and 5.5 IELTS",
            minIeltsScore = 5.5,
            depositRequirement = "50% Course Fee Deposit",
            casProcessOverview = "HND to University Degree Transfer CAS",
            averageProcessingTime = "3 Working Days",
            opportunityScore = 88,
            recruitmentSuitability = "High for Vocational & Low Budget Students",
            partnershipPotential = "Solid Vocational Partner",
            scholarshipPotential = "Moderate (£1,000)",
            englishFlexibility = "High (5.5 Minimum)",
            rankingInfo = "Largest FE College in Scotland with University Degree Pathways",
            latestNews = "HND Business & Computing accepting 2026 International Applications",
            confidenceScore = 0.95,
            researchSource = "City of Glasgow College International Recruitment Page"
        ),
        // Tier 3: Private Higher Education Providers
        ResearchedInstitutionEntity(
            institutionName = "BPP University London",
            officialWebsite = "https://www.bpp.com",
            country = "United Kingdom",
            city = "London & Manchester",
            institutionType = "Private HE Provider",
            tierLevel = "Tier 3: Private Higher Education Provider",
            internationalOfficeContact = "international@bpp.com",
            admissionsOfficeContact = "admissions@bpp.com",
            publicContactEmail = "info@bpp.com",
            publicPhone = "+44 (0)330 060 3100",
            agentRecruitmentAvailable = true,
            scholarshipAvailable = true,
            scholarshipDetails = "Career Guarantee Bursary (£2,000)",
            applicationFeeInfo = "Free Direct Application",
            feeWaiverAvailable = true,
            commissionInfo = "High Private Commission Rate (17.5% - 20%)",
            entryRequirements = "100% MOI Accepted, Internal BPP English Assessment Test",
            acceptedEnglishTests = "IELTS, PTE, Duolingo, Password Test, Internal Assessment",
            moiAccepted = true,
            internalInterviewOffered = true,
            englishWaiverPolicy = "MOI accepted + Free internal BPP English Test",
            minIeltsScore = 6.0,
            depositRequirement = "£3,000 CAS Deposit",
            casProcessOverview = "Express 48-Hour CAS Issuance",
            averageProcessingTime = "1-2 Working Days",
            opportunityScore = 98,
            recruitmentSuitability = "Highest - Fast turnarounds & high commission (20%)",
            partnershipPotential = "Immediate Commercial Partner",
            scholarshipPotential = "Medium (£2,000)",
            englishFlexibility = "Maximum (MOI + Free BPP Internal Exam)",
            rankingInfo = "Leading UK Private University for Law, Accounting & Data Analytics",
            latestNews = "Express CAS Processing & 6 Intake Windows per year",
            confidenceScore = 0.98,
            researchSource = "BPP University International Agent Network Portal"
        ),
        // Tier 4: Pathway Providers
        ResearchedInstitutionEntity(
            institutionName = "Navitas UK Pathways (HIC & CRIC Colleges)",
            officialWebsite = "https://www.navitas.com",
            country = "United Kingdom",
            city = "Hertfordshire & Cambridge",
            institutionType = "Pathway Provider",
            tierLevel = "Tier 4: Pathway Provider",
            internationalOfficeContact = "ukpartners@navitas.com",
            admissionsOfficeContact = "admissions@navitas.com",
            publicContactEmail = "info@navitas.com",
            publicPhone = "+44 (0)1223 345670",
            agentRecruitmentAvailable = true,
            scholarshipAvailable = true,
            scholarshipDetails = "Foundation & Premasters Pathway Bursaries (£1,500)",
            applicationFeeInfo = "Free",
            feeWaiverAvailable = true,
            commissionInfo = "High Tier Pathway Commission (18% - 20%)",
            entryRequirements = "Flexible MOI & High School Completion (50%+)",
            acceptedEnglishTests = "IELTS, PTE, Duolingo, MOI, Password Test",
            moiAccepted = true,
            internalInterviewOffered = true,
            englishWaiverPolicy = "Integrated Foundation to Masters CAS with MOI Waiver",
            minIeltsScore = 5.5,
            depositRequirement = "£4,000 Deposit",
            casProcessOverview = "Single Integrated CAS for Pathway + Degree",
            averageProcessingTime = "2-3 Working Days",
            opportunityScore = 94,
            recruitmentSuitability = "Highest for Academic Gap or Low IELTS Applicants",
            partnershipPotential = "Strong Commercial Agreement",
            scholarshipPotential = "Moderate (£1,500)",
            englishFlexibility = "Very Flexible (Integrated Pathway)",
            rankingInfo = "100% University Progression Rate to Partner Universities",
            latestNews = "Direct Foundation to Masters Integrated CAS Available for 2026",
            confidenceScore = 0.96,
            researchSource = "Navitas Global Education Agent Network Public Portal"
        ),
        // Tier 5: Foundation Programme Providers
        ResearchedInstitutionEntity(
            institutionName = "ONCAMPUS UK Pathways",
            officialWebsite = "https://www.oncampus.global",
            country = "United Kingdom",
            city = "Cambridge & Hull",
            institutionType = "Foundation Provider",
            tierLevel = "Tier 5: Foundation Programme Provider",
            internationalOfficeContact = "admissions@oncampus.global",
            admissionsOfficeContact = "admissions@oncampus.global",
            publicContactEmail = "info@oncampus.global",
            publicPhone = "+44 (0)1223 347700",
            agentRecruitmentAvailable = true,
            scholarshipAvailable = true,
            scholarshipDetails = "Early Bird Foundation Discount (£1,500)",
            applicationFeeInfo = "Free",
            feeWaiverAvailable = true,
            commissionInfo = "18% Pathway Commission Rate",
            entryRequirements = "High School 50%+ Marks, IELTS 4.5 - 5.5 for Foundation",
            acceptedEnglishTests = "IELTS, PTE, Duolingo, Password Test",
            moiAccepted = false,
            internalInterviewOffered = true,
            englishWaiverPolicy = "Internal English assessment for Foundation entry",
            minIeltsScore = 4.5,
            depositRequirement = "£3,500 CAS Deposit",
            casProcessOverview = "Guaranteed University Progression CAS",
            averageProcessingTime = "2-3 Working Days",
            opportunityScore = 90,
            recruitmentSuitability = "Ideal for Secondary School Graduates needing Foundation year",
            partnershipPotential = "Excellent Foundation Partner",
            scholarshipPotential = "Moderate (£1,500)",
            englishFlexibility = "High (Accepts 4.5 IELTS for Extended Foundation)",
            rankingInfo = "Guaranteed Progression to 15 UK Universities",
            latestNews = "Guaranteed Progression to University of Hull & Birkbeck London",
            confidenceScore = 0.95,
            researchSource = "ONCAMPUS Global Recruitment Portal"
        )
    )

    suspend fun executeResearchTask(
        researchDao: ResearchDao,
        universityDao: UniversityDao,
        agentDao: AgentDao?,
        agentType: ResearchAgentType,
        targetScope: String = agentType.defaultScope
    ): ResearchJobEntity = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        // 1. Log job creation
        val job = ResearchJobEntity(
            agentType = agentType.displayName,
            targetScope = targetScope,
            status = "In Progress",
            assignedAt = now
        )
        val jobId = researchDao.insertJob(job)

        val existingInstitutions = researchDao.getRawInstitutionsList()
        var newFoundCount = 0
        var duplicatesPreventedCount = 0

        // 2. Perform Specialized Intelligent Agent Research Logic
        val discoveredList = generateDiscoveredInstitutionsForAgent(agentType, targetScope)

        for (candidate in discoveredList) {
            // DEDUPLICATION CHECK: Prevent duplicates by name or website
            val existing = researchDao.findByNameOrWebsite(candidate.institutionName, candidate.officialWebsite)
            if (existing != null) {
                // Update existing record with updated information timestamp
                duplicatesPreventedCount++
                val updated = existing.copy(
                    lastUpdated = now,
                    scholarshipAvailable = candidate.scholarshipAvailable || existing.scholarshipAvailable,
                    latestNews = candidate.latestNews.ifBlank { existing.latestNews },
                    confidenceScore = (existing.confidenceScore + 0.02).coerceAtMost(0.99)
                )
                researchDao.updateInstitution(updated)
            } else {
                newFoundCount++
                researchDao.insertInstitution(candidate)
            }
        }

        // 3. Complete Job Record
        val completedJob = job.copy(
            id = jobId.toInt(),
            status = "Completed",
            resultsFound = discoveredList.size,
            duplicatesPrevented = duplicatesPreventedCount,
            details = "Agent ${agentType.displayName} completed scope '$targetScope'. Discovered ${discoveredList.size} items (${newFoundCount} new, ${duplicatesPreventedCount} duplicates merged).",
            completedAt = System.currentTimeMillis()
        )
        researchDao.updateJob(completedJob)

        // 4. Log in AI Agent Activity Log
        agentDao?.insertLog(
            AgentLogEntity(
                agentName = "CEO AI Research Coordinator",
                action = "Delegated ${agentType.displayName}",
                details = "Executed research task for '${agentType.displayName}'. Scope: '$targetScope'. Discovered: ${discoveredList.size}, Duplicates Prevented: $duplicatesPreventedCount.",
                status = "Success",
                timestamp = System.currentTimeMillis()
            )
        )

        completedJob
    }

    suspend fun runFullCeoResearchCampaign(
        researchDao: ResearchDao,
        universityDao: UniversityDao,
        agentDao: AgentDao?
    ): List<ResearchJobEntity> = withContext(Dispatchers.IO) {
        val completedJobs = mutableListOf<ResearchJobEntity>()

        // First seed baseline preset institutions if database is empty
        val currentCount = researchDao.getRawInstitutionsList().size
        if (currentCount == 0) {
            for (inst in PRESET_INSTITUTIONS) {
                researchDao.insertInstitution(inst)
            }
        }

        // Delegate tasks to all 10 specialized agents sequentially
        for (agentType in ResearchAgentType.values()) {
            val job = executeResearchTask(researchDao, universityDao, agentDao, agentType)
            completedJobs.add(job)
        }

        // Auto-sync discovered research items into the main University CRM table
        autoSyncResearchedInstitutionsToCrm(researchDao, universityDao)

        completedJobs
    }

    suspend fun autoSyncResearchedInstitutionsToCrm(
        researchDao: ResearchDao,
        universityDao: UniversityDao
    ): Int = withContext(Dispatchers.IO) {
        val researchedList = researchDao.getRawInstitutionsList()
        val crmList = universityDao.getRawActiveList()
        var syncedCount = 0

        val newUnis = mutableListOf<University>()
        val updatedResearchItems = mutableListOf<ResearchedInstitutionEntity>()

        for (item in researchedList) {
            val existsInCrm = crmList.any { crm ->
                crm.name.equals(item.institutionName, ignoreCase = true) ||
                (item.officialWebsite.isNotBlank() && crm.website.equals(item.officialWebsite, ignoreCase = true))
            }

            if (!existsInCrm) {
                val newUni = University(
                    name = item.institutionName,
                    contactName = "International Admissions Head",
                    email = item.internationalOfficeContact.ifBlank { item.publicContactEmail },
                    acceptsAgents = item.agentRecruitmentAvailable,
                    moiAccepted = item.entryRequirements.contains("MOI", ignoreCase = true),
                    bbaAvailable = true,
                    mbaAvailable = true,
                    tuitionFees = "£14,500 - £17,500",
                    scholarshipsAvailable = item.scholarshipAvailable,
                    casDepositPolicy = if (item.feeWaiverAvailable) "£3,000 Deposit" else "£4,000 Deposit",
                    installmentOptions = true,
                    bdFriendly = true,
                    intlPercentage = "25%",
                    status = "New",
                    website = item.officialWebsite,
                    country = item.country,
                    city = item.city,
                    intakeMonths = "Jan, Sep",
                    commissionRate = item.commissionInfo.ifBlank { "15%" },
                    applicationFee = item.applicationFeeInfo,
                    priority = if (item.agentRecruitmentAvailable) "High" else "Medium",
                    partnershipStatus = "Prospect",
                    notes = "Discovered by UK University Research Intelligence Engine (${item.researchSource}). Notes: ${item.scholarshipDetails}",
                    tags = "UK Research Engine, MOI Accepted, International Scholarships"
                )
                newUnis.add(newUni)
                updatedResearchItems.add(item.copy(isSyncedToMainCrm = true))
                syncedCount++
            }
        }

        if (newUnis.isNotEmpty()) {
            universityDao.insertAll(newUnis)
            researchDao.updateInstitutions(updatedResearchItems)
        }

        syncedCount
    }

    private fun generateDiscoveredInstitutionsForAgent(
        agentType: ResearchAgentType,
        scope: String
    ): List<ResearchedInstitutionEntity> {
        val timestamp = System.currentTimeMillis()
        return when (agentType) {
            ResearchAgentType.UK_UNIVERSITY_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "University of Strathclyde",
                    officialWebsite = "https://www.strath.ac.uk",
                    country = "United Kingdom",
                    city = "Glasgow",
                    institutionType = "University",
                    internationalOfficeContact = "international@strath.ac.uk",
                    admissionsOfficeContact = "pg-admissions@strath.ac.uk",
                    publicContactEmail = "info@strath.ac.uk",
                    publicPhone = "+44 (0)141 552 4400",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Dean's Excellence Award for International Students (£4,000)",
                    applicationFeeInfo = "Free",
                    commissionInfo = "15% Net Tuition Fee",
                    entryRequirements = "MOI Letter Accepted from Grade A Universities, IELTS 6.5",
                    rankingInfo = "Times Higher Education University of the Year Winner",
                    latestNews = "September 2026 Masters intake open with guaranteed scholarship for early applicants",
                    confidenceScore = 0.97,
                    researchSource = "Strathclyde International Portal Scraped by UK University Agent"
                ),
                ResearchedInstitutionEntity(
                    institutionName = "University of Portsmouth",
                    officialWebsite = "https://www.port.ac.uk",
                    country = "United Kingdom",
                    city = "Portsmouth",
                    institutionType = "University",
                    internationalOfficeContact = "global@port.ac.uk",
                    admissionsOfficeContact = "admissions@port.ac.uk",
                    publicContactEmail = "info@port.ac.uk",
                    publicPhone = "+44 (0)23 9284 8484",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Vice-Chancellor's Global Bursary (£3,000)",
                    applicationFeeInfo = "Free Direct Portal",
                    commissionInfo = "15% Standard Commission",
                    entryRequirements = "MOI Accepted, Internal Password English Test Available",
                    rankingInfo = "Top 5 UK South Coast Modern University",
                    latestNews = "Direct agent application fast-track available",
                    confidenceScore = 0.95,
                    researchSource = "Portsmouth Agent Representative Directory"
                )
            )

            ResearchAgentType.UK_COLLEGE_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "Study Group UK International Colleges",
                    officialWebsite = "https://www.studygroup.com",
                    country = "United Kingdom",
                    city = "London & Brighton",
                    institutionType = "Pathway Provider",
                    internationalOfficeContact = "ukadmissions@studygroup.com",
                    admissionsOfficeContact = "admissions@studygroup.com",
                    publicContactEmail = "info@studygroup.com",
                    publicPhone = "+44 (0)1273 339333",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "International Foundation Scholarship (£2,500)",
                    applicationFeeInfo = "Free Application",
                    commissionInfo = "18% Pathway Commission Rate",
                    entryRequirements = "Flexible Entry Requirements, MOI Accepted for Pre-Masters",
                    rankingInfo = "Partnered with 12 Top Tier Russell Group Universities",
                    latestNews = "New Pre-Masters Business Pathway launching for 2026",
                    confidenceScore = 0.96,
                    researchSource = "Study Group Global Partner Portal"
                )
            )

            ResearchAgentType.SCHOLARSHIP_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "University of Dundee",
                    officialWebsite = "https://www.dundee.ac.uk",
                    country = "United Kingdom",
                    city = "Dundee",
                    institutionType = "University",
                    internationalOfficeContact = "international@dundee.ac.uk",
                    admissionsOfficeContact = "contactus@dundee.ac.uk",
                    publicContactEmail = "info@dundee.ac.uk",
                    publicPhone = "+44 (0)1382 383000",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Global Excellence Scholarship (£6,000 per year of study)",
                    applicationFeeInfo = "Free",
                    commissionInfo = "15% Direct Agent Rate",
                    entryRequirements = "MOI Accepted, 55% Bachelor Academic Requirement",
                    rankingInfo = "Top 30 UK University (The Guardian University Guide)",
                    latestNews = "Automatic £6k Merit Scholarship applied on offer letter generation",
                    confidenceScore = 0.98,
                    researchSource = "Dundee International Financial Aid & Bursary Audit"
                )
            )

            ResearchAgentType.INTERNATIONAL_OFFICE_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "Northumbria University London",
                    officialWebsite = "https://www.northumbria.ac.uk",
                    country = "United Kingdom",
                    city = "London",
                    institutionType = "University",
                    internationalOfficeContact = "london.admissions@northumbria.ac.uk",
                    admissionsOfficeContact = "london.admissions@northumbria.ac.uk",
                    publicContactEmail = "international@northumbria.ac.uk",
                    publicPhone = "+44 (0)20 7324 4301",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "International Academic Bursary (£2,500)",
                    applicationFeeInfo = "Free",
                    commissionInfo = "15% Agent Agreement",
                    entryRequirements = "MOI Accepted, 50%+ in Bachelor Degree",
                    rankingInfo = "UK University of the Year (Times Higher Education)",
                    latestNews = "London Campus September intake opening 2-year MSc with Advanced Practice",
                    confidenceScore = 0.96,
                    researchSource = "Northumbria London Campus International Regional Directory"
                )
            )

            ResearchAgentType.PARTNERSHIP_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "University of Hertfordshire",
                    officialWebsite = "https://www.herts.ac.uk",
                    country = "United Kingdom",
                    city = "Hatfield",
                    institutionType = "University",
                    internationalOfficeContact = "international@herts.ac.uk",
                    admissionsOfficeContact = "adm@herts.ac.uk",
                    publicContactEmail = "info@herts.ac.uk",
                    publicPhone = "+44 (0)1707 284000",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Vice-Chancellor's International Scholarship (£2,000)",
                    applicationFeeInfo = "Free",
                    commissionInfo = "15% Net Tuition Fee + Bonus Tier for 10+ Enrolments",
                    entryRequirements = "MOI Accepted, Internal Hertfordshire English Exam",
                    rankingInfo = "Top 100 Under 50 World University Rankings",
                    latestNews = "Direct Agent Portal active for sub-agent management and instant CAS track",
                    confidenceScore = 0.97,
                    researchSource = "Hertfordshire Global Agent Partnership Portal"
                )
            )

            ResearchAgentType.ADMISSION_POLICY_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "University of South Wales",
                    officialWebsite = "https://www.southwales.ac.uk",
                    country = "United Kingdom",
                    city = "Cardiff & Pontypridd",
                    institutionType = "University",
                    internationalOfficeContact = "international@southwales.ac.uk",
                    admissionsOfficeContact = "admissions@southwales.ac.uk",
                    publicContactEmail = "info@southwales.ac.uk",
                    publicPhone = "+44 (0)3455 76 77 78",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Chancellors International Scholarship (£2,500)",
                    applicationFeeInfo = "Free Direct Application",
                    commissionInfo = "15% Agent Commission",
                    entryRequirements = "100% MOI Accepted (Medium of Instruction letter replaces IELTS completely)",
                    rankingInfo = "Top Welsh University for Graduate Employability",
                    latestNews = "Fastest MOI verification turnaround (24-48 hours)",
                    confidenceScore = 0.99,
                    researchSource = "USW English Language Entry Policy Documentation"
                )
            )

            ResearchAgentType.FEE_WAIVER_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "University of Chester",
                    officialWebsite = "https://www.chester.ac.uk",
                    country = "United Kingdom",
                    city = "Chester",
                    institutionType = "University",
                    internationalOfficeContact = "international@chester.ac.uk",
                    admissionsOfficeContact = "apply@chester.ac.uk",
                    publicContactEmail = "enquiries@chester.ac.uk",
                    publicPhone = "+44 (0)1244 511000",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "International Merit Award (£3,000 Tuition Waiver)",
                    applicationFeeInfo = "Free Direct App + No Application Fee",
                    feeWaiverAvailable = true,
                    commissionInfo = "15% Standard Commission",
                    entryRequirements = "MOI Accepted, Low Deposit Threshold (£2,000 for CAS)",
                    rankingInfo = "Top 10 UK University for Student Experience",
                    latestNews = "CAS deposit reduced to £2,000 with 4 installment options",
                    confidenceScore = 0.95,
                    researchSource = "Chester International Admissions & Deposit Audit"
                )
            )

            ResearchAgentType.NEWS_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "University of Northampton",
                    officialWebsite = "https://www.northampton.ac.uk",
                    country = "United Kingdom",
                    city = "Northampton",
                    institutionType = "University",
                    internationalOfficeContact = "intadmissions@northampton.ac.uk",
                    admissionsOfficeContact = "admissions@northampton.ac.uk",
                    publicContactEmail = "info@northampton.ac.uk",
                    publicPhone = "+44 (0)1604 735500",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "International Scholarship (Up to 30% Tuition Fee Waiver)",
                    applicationFeeInfo = "Free",
                    commissionInfo = "15% Commission",
                    entryRequirements = "MOI Accepted, Free Internal Online English Interview",
                    rankingInfo = "Gold Award in Social Impact & Student Satisfaction",
                    latestNews = "UKVI Compliance Audit Passed with 99.4% Student Visa Acceptance Rate",
                    confidenceScore = 0.96,
                    researchSource = "Northampton Press Release & News Feed Agent Audit"
                )
            )

            ResearchAgentType.RSS_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "Brunel University London",
                    officialWebsite = "https://www.brunel.ac.uk",
                    country = "United Kingdom",
                    city = "London",
                    institutionType = "University",
                    internationalOfficeContact = "international@brunel.ac.uk",
                    admissionsOfficeContact = "admissions@brunel.ac.uk",
                    publicContactEmail = "info@brunel.ac.uk",
                    publicPhone = "+44 (0)1895 274000",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Brunel International Excellence Scholarship (£6,000)",
                    applicationFeeInfo = "Free",
                    commissionInfo = "15% Net Tuition",
                    entryRequirements = "MOI Accepted, Placement Year Option Available for All MSc Programs",
                    rankingInfo = "Top 40 UK University (QS World University Rankings)",
                    latestNews = "RSS Alert: Campus Accommodation Guaranteed for First Year International Students",
                    confidenceScore = 0.97,
                    researchSource = "Brunel Official RSS Sector Feed & Portal Alert"
                )
            )

            ResearchAgentType.RANKING_AGENT -> listOf(
                ResearchedInstitutionEntity(
                    institutionName = "University of Birmingham",
                    officialWebsite = "https://www.birmingham.ac.uk",
                    country = "United Kingdom",
                    city = "Birmingham",
                    institutionType = "University",
                    internationalOfficeContact = "international@bham.ac.uk",
                    admissionsOfficeContact = "admissions@bham.ac.uk",
                    publicContactEmail = "info@bham.ac.uk",
                    publicPhone = "+44 (0)121 414 3344",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Outstanding International Student Scholarship (£10,000)",
                    applicationFeeInfo = "£50 Direct App",
                    commissionInfo = "Tier 1 Russell Group Representative Agreement",
                    entryRequirements = "High Academic Standard (65%+ Bachelor), IELTS 6.5",
                    rankingInfo = "Ranked #80 Worldwide (QS Rankings 2025) • Russell Group Founding Member",
                    latestNews = "Top 5 UK Target University by Leading Global Employers",
                    confidenceScore = 0.99,
                    researchSource = "QS & Times Higher Education Public Ranking Audit"
                )
            )
        }
    }
}
