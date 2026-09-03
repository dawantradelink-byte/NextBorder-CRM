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
import java.util.UUID

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
        // Insert a large number of discovered institutions
        val numInstitutions = 500

        for (i in 1..numInstitutions) {
            researchDao.insertInstitution(
                ResearchedInstitutionEntity(
                    institutionName = "Test Uni $i",
                    officialWebsite = "https://www.testuni$i.edu",
                    country = "USA",
                    city = "Test City",
                    institutionType = "University",
                    internationalOfficeContact = "int$i@testuni.edu",
                    admissionsOfficeContact = "adm$i@testuni.edu",
                    publicContactEmail = "info$i@testuni.edu",
                    publicPhone = "+1 555 100 $i",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Test Scholarship",
                    applicationFeeInfo = "Free",
                    commissionInfo = "15%",
                    entryRequirements = "Standard",
                    rankingInfo = "Top 100",
                    latestNews = "Test News",
                    confidenceScore = 0.95,
                    researchSource = "Performance Test"
                )
            )
        }

        val startTime = System.currentTimeMillis()
        val syncedCount = CeoAiResearchCoordinator.autoSyncResearchedInstitutionsToCrm(researchDao, universityDao)
        val endTime = System.currentTimeMillis()

        println("Synchronized $syncedCount institutions in ${endTime - startTime} ms")
        assert(syncedCount == numInstitutions)
    }
}
