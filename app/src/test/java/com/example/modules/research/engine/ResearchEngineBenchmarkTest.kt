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
import org.robolectric.annotation.Config
import kotlin.system.measureTimeMillis

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ResearchEngineBenchmarkTest {
    private lateinit var database: AppDatabase
    private lateinit var universityDao: UniversityDao
    private lateinit var researchDao: ResearchDao


    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        universityDao = database.universityDao()
        researchDao = database.researchDao()

    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun benchmarkAutoSync() = runBlocking {
        // Insert 100 dummy researched institutions
        for (i in 1..100) {
            researchDao.insertInstitution(
                ResearchedInstitutionEntity(
                    institutionName = "Benchmark University $i",
                    officialWebsite = "https://www.benchmark$i.ac.uk",
                    country = "United Kingdom",
                    city = "Benchmark City",
                    institutionType = "University",
                    internationalOfficeContact = "international@benchmark$i.ac.uk",
                    admissionsOfficeContact = "admissions@benchmark$i.ac.uk",
                    publicContactEmail = "info@benchmark$i.ac.uk",
                    publicPhone = "+44 (0)123 456 $i",
                    agentRecruitmentAvailable = true,
                    scholarshipAvailable = true,
                    scholarshipDetails = "Up to £2000",
                    applicationFeeInfo = "Free",
                    feeWaiverAvailable = true,
                    commissionInfo = "15%",
                    entryRequirements = "MOI",
                    rankingInfo = "Top 100",
                    latestNews = "",
                    rssFeedUrl = "",
                    lastUpdated = System.currentTimeMillis(),
                    confidenceScore = 0.95,
                    researchSource = "Benchmark Engine",
                    isSyncedToMainCrm = false
                )
            )
        }

        // Measure time taken to sync
        val timeTaken = measureTimeMillis {
            CeoAiResearchCoordinator.autoSyncResearchedInstitutionsToCrm(researchDao, universityDao)
        }

        println("BENCHMARK_RESULT_autoSyncResearchedInstitutionsToCrm: $timeTaken ms")

        val syncedCount = database.universityDao().getRawActiveList().size
        println("Synced count: $syncedCount")
        assert(syncedCount == 100)
    }
}
