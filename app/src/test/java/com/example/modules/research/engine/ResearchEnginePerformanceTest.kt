package com.example.modules.research.engine

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.UniversityDao
import com.example.modules.research.data.ResearchDao
import com.example.modules.research.model.ResearchedInstitutionEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.system.measureTimeMillis

@RunWith(RobolectricTestRunner::class)
class ResearchEnginePerformanceTest {

    private lateinit var db: AppDatabase
    private lateinit var universityDao: UniversityDao
    private lateinit var researchDao: ResearchDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        universityDao = db.universityDao()
        researchDao = db.researchDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun benchmarkAutoSyncResearchedInstitutionsToCrm() = runBlocking {
        // Insert 100 dummy research institutions
        for (i in 1..100) {
            val inst = ResearchedInstitutionEntity(
                institutionName = "University $i",
                officialWebsite = "https://www.uni$i.ac.uk",
                country = "United Kingdom",
                city = "City $i",
                institutionType = "University",
                tierLevel = "Tier 1",
                internationalOfficeContact = "int@uni$i.ac.uk",
                admissionsOfficeContact = "adm@uni$i.ac.uk",
                publicContactEmail = "info@uni$i.ac.uk",
                publicPhone = "1234567890",
                agentRecruitmentAvailable = true,
                scholarshipAvailable = true,
                scholarshipDetails = "Scholarship $i",
                applicationFeeInfo = "Free",
                feeWaiverAvailable = true,
                commissionInfo = "15%",
                entryRequirements = "MOI",
                acceptedEnglishTests = "IELTS",
                moiAccepted = true,
                internalInterviewOffered = true,
                englishWaiverPolicy = "Waiver",
                minIeltsScore = 6.0,
                depositRequirement = "Deposit",
                casProcessOverview = "CAS",
                averageProcessingTime = "Time",
                opportunityScore = 100,
                recruitmentSuitability = "High",
                partnershipPotential = "High",
                scholarshipPotential = "High",
                englishFlexibility = "High",
                rankingInfo = "Rank $i",
                latestNews = "News $i",
                confidenceScore = 0.9,
                researchSource = "Source $i",
                isSyncedToMainCrm = false
            )
            researchDao.insertInstitution(inst)
        }

        val time = measureTimeMillis {
            CeoAiResearchCoordinator.autoSyncResearchedInstitutionsToCrm(researchDao, universityDao)
        }

        println("autoSyncResearchedInstitutionsToCrm took ${time}ms for 100 records")
    }
}
