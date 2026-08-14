package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.modules.research.data.ResearchDao
import com.example.modules.research.model.ResearchJobEntity
import com.example.modules.research.model.ResearchedInstitutionEntity

@Database(
    entities = [
        University::class,
        TodoItem::class,
        ContactLog::class,
        UniversityNote::class,
        MeetingRecord::class,
        AgentMemoryEntity::class,
        AgentLogEntity::class,
        UniversityReplyEntity::class,
        ReminderEntity::class,
        ResearchedInstitutionEntity::class,
        ResearchJobEntity::class,
        StudentProfileEntity::class,
        ExecutiveMemoryEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun universityDao(): UniversityDao
    abstract fun todoDao(): TodoDao
    abstract fun agentDao(): AgentDao
    abstract fun reminderDao(): ReminderDao
    abstract fun researchDao(): ResearchDao
    abstract fun studentDao(): StudentDao
    abstract fun executiveMemoryDao(): ExecutiveMemoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Version 1 to 2 migration handling
                db.execSQL("ALTER TABLE universities ADD COLUMN website TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE universities ADD COLUMN country TEXT NOT NULL DEFAULT 'United Kingdom'")
                db.execSQL("ALTER TABLE universities ADD COLUMN city TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE universities ADD COLUMN intakeMonths TEXT NOT NULL DEFAULT 'Jan, Sep'")
                db.execSQL("ALTER TABLE universities ADD COLUMN commissionRate TEXT NOT NULL DEFAULT '15%'")
                db.execSQL("ALTER TABLE universities ADD COLUMN applicationFee TEXT NOT NULL DEFAULT 'Free'")
                db.execSQL("ALTER TABLE universities ADD COLUMN priority TEXT NOT NULL DEFAULT 'Medium'")
                db.execSQL("ALTER TABLE universities ADD COLUMN partnershipStatus TEXT NOT NULL DEFAULT 'Prospect'")
                db.execSQL("ALTER TABLE universities ADD COLUMN lastContactedDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE universities ADD COLUMN nextFollowUpDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE universities ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE universities ADD COLUMN tags TEXT NOT NULL DEFAULT 'Middle-Class, Easy Admission'")
                db.execSQL("ALTER TABLE universities ADD COLUMN assignedCounselor TEXT NOT NULL DEFAULT 'Ishak Dewan'")
                db.execSQL("ALTER TABLE universities ADD COLUMN whatsappNumber TEXT NOT NULL DEFAULT '+447700900077'")
                db.execSQL("ALTER TABLE universities ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE universities ADD COLUMN createdAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
                db.execSQL("ALTER TABLE universities ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Version 2 to 3 migration handling
                db.execSQL("CREATE TABLE IF NOT EXISTS `contact_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `universityId` INTEGER NOT NULL, `type` TEXT NOT NULL, `summary` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `university_notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `universityId` INTEGER NOT NULL, `content` TEXT NOT NULL, `author` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `meeting_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `universityId` INTEGER NOT NULL, `title` TEXT NOT NULL, `meetingDate` INTEGER NOT NULL, `meetingLink` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)")
                
                // Todo Item columns
                db.execSQL("ALTER TABLE todo_items ADD COLUMN priority TEXT NOT NULL DEFAULT 'Medium'")
                db.execSQL("ALTER TABLE todo_items ADD COLUMN universityId INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE todo_items ADD COLUMN completedAt INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE todo_items ADD COLUMN recurringRule TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE todo_items ADD COLUMN createdAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `agent_memories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `agentName` TEXT NOT NULL, `agentType` TEXT NOT NULL, `category` TEXT NOT NULL, `content` TEXT NOT NULL, `impactRating` INTEGER NOT NULL DEFAULT 5, `timestamp` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `agent_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `agentName` TEXT NOT NULL, `action` TEXT NOT NULL, `details` TEXT NOT NULL, `status` TEXT NOT NULL DEFAULT 'Success', `timestamp` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `university_replies` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `universityId` INTEGER NOT NULL, `universityName` TEXT NOT NULL, `subject` TEXT NOT NULL, `snippet` TEXT NOT NULL, `sentiment` TEXT NOT NULL DEFAULT 'Pending', `isRead` INTEGER NOT NULL DEFAULT 0, `receivedAt` INTEGER NOT NULL, `suggestedNextAction` TEXT NOT NULL DEFAULT '')")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE universities ADD COLUMN greylistReason TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE universities ADD COLUMN greylistRefusalDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE universities ADD COLUMN greylistConversation TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE universities ADD COLUMN greylistConfidenceScore REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE universities ADD COLUMN greylistSuggestedFollowUpDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE universities ADD COLUMN greylistRecommendedStrategy TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE universities ADD COLUMN greylistAiAnalysis TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE universities ADD COLUMN blacklistReason TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE universities ADD COLUMN blacklistDate INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `reminders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `category` TEXT NOT NULL DEFAULT 'Business', `priority` TEXT NOT NULL DEFAULT 'Medium', `recurrence` TEXT NOT NULL DEFAULT 'One-time', `customDate` TEXT NOT NULL DEFAULT '', `customTime` TEXT NOT NULL DEFAULT '09:00', `triggerTimeMs` INTEGER NOT NULL, `repeatIntervalMinutes` INTEGER NOT NULL DEFAULT 15, `isCompleted` INTEGER NOT NULL DEFAULT 0, `completedAt` INTEGER DEFAULT NULL, `universityId` INTEGER DEFAULT NULL, `notes` TEXT NOT NULL DEFAULT '', `snoozeCount` INTEGER NOT NULL DEFAULT 0, `createdAt` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `researched_institutions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `institutionName` TEXT NOT NULL, `officialWebsite` TEXT NOT NULL DEFAULT '', `country` TEXT NOT NULL DEFAULT 'United Kingdom', `city` TEXT NOT NULL DEFAULT '', `institutionType` TEXT NOT NULL DEFAULT 'University', `internationalOfficeContact` TEXT NOT NULL DEFAULT 'international@institution.ac.uk', `admissionsOfficeContact` TEXT NOT NULL DEFAULT 'admissions@institution.ac.uk', `publicContactEmail` TEXT NOT NULL DEFAULT 'info@institution.ac.uk', `publicPhone` TEXT NOT NULL DEFAULT '+44 (0)20 7946 0991', `agentRecruitmentAvailable` INTEGER NOT NULL DEFAULT 1, `scholarshipAvailable` INTEGER NOT NULL DEFAULT 1, `scholarshipDetails` TEXT NOT NULL DEFAULT '', `applicationFeeInfo` TEXT NOT NULL DEFAULT 'Free / Waiver Available', `feeWaiverAvailable` INTEGER NOT NULL DEFAULT 1, `commissionInfo` TEXT NOT NULL DEFAULT '', `entryRequirements` TEXT NOT NULL DEFAULT '', `rankingInfo` TEXT NOT NULL DEFAULT '', `latestNews` TEXT NOT NULL DEFAULT '', `rssFeedUrl` TEXT NOT NULL DEFAULT '', `lastUpdated` INTEGER NOT NULL, `confidenceScore` REAL NOT NULL DEFAULT 0.95, `researchSource` TEXT NOT NULL DEFAULT '', `isSyncedToMainCrm` INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `research_jobs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `agentType` TEXT NOT NULL, `targetScope` TEXT NOT NULL, `status` TEXT NOT NULL DEFAULT 'Pending', `resultsFound` INTEGER NOT NULL DEFAULT 0, `duplicatesPrevented` INTEGER NOT NULL DEFAULT 0, `details` TEXT NOT NULL DEFAULT '', `assignedAt` INTEGER NOT NULL, `completedAt` INTEGER DEFAULT NULL)")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN tierLevel TEXT NOT NULL DEFAULT 'Tier 1: Mid-ranked UK University'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN acceptedEnglishTests TEXT NOT NULL DEFAULT 'IELTS, PTE, Duolingo, MOI, Internal English Interview, Password Test'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN moiAccepted INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN internalInterviewOffered INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN englishWaiverPolicy TEXT NOT NULL DEFAULT 'MOI Accepted within 5 years of graduation'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN minIeltsScore REAL NOT NULL DEFAULT 6.0")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN depositRequirement TEXT NOT NULL DEFAULT '£3,000 CAS Deposit'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN casProcessOverview TEXT NOT NULL DEFAULT 'Pre-CAS interview required, Bank balance 28-day check'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN averageProcessingTime TEXT NOT NULL DEFAULT '3-5 Working Days'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN opportunityScore INTEGER NOT NULL DEFAULT 90")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN recruitmentSuitability TEXT NOT NULL DEFAULT 'High - Flexible Admissions & Direct Agent Portal'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN partnershipPotential TEXT NOT NULL DEFAULT 'High B2B Opportunity'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN scholarshipPotential TEXT NOT NULL DEFAULT 'High (£3,000 - £5,000 Bursary)'")
                db.execSQL("ALTER TABLE researched_institutions ADD COLUMN englishFlexibility TEXT NOT NULL DEFAULT 'Very Flexible (MOI + Password Test + Interview)'")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `student_profiles` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `country` TEXT NOT NULL DEFAULT 'Bangladesh',
                        `academicBackground` TEXT NOT NULL DEFAULT 'BSc in Computer Science',
                        `sscScore` TEXT NOT NULL DEFAULT 'GPA 5.00',
                        `hscScore` TEXT NOT NULL DEFAULT 'GPA 5.00',
                        `bachelorDegree` TEXT NOT NULL DEFAULT 'BSc Computer Science & Engineering (CGPA 3.40)',
                        `masterDegree` TEXT NOT NULL DEFAULT '',
                        `cgpa` REAL NOT NULL DEFAULT 3.40,
                        `gapYears` INTEGER NOT NULL DEFAULT 1,
                        `workExperience` TEXT NOT NULL DEFAULT '1.5 Years Software Engineer / IT Specialist',
                        `preferredIntake` TEXT NOT NULL DEFAULT 'September 2026',
                        `preferredSubject` TEXT NOT NULL DEFAULT 'Computer Science / Data Science',
                        `preferredCity` TEXT NOT NULL DEFAULT 'London / Manchester / Birmingham',
                        `preferredCountry` TEXT NOT NULL DEFAULT 'United Kingdom',
                        `budget` REAL NOT NULL DEFAULT 16000.0,
                        `englishQualification` TEXT NOT NULL DEFAULT 'MOI & IELTS',
                        `ieltsOverall` REAL NOT NULL DEFAULT 6.5,
                        `moiAvailable` INTEGER NOT NULL DEFAULT 1,
                        `duolingoScore` INTEGER NOT NULL DEFAULT 120,
                        `pteScore` INTEGER NOT NULL DEFAULT 62,
                        `toeflScore` INTEGER NOT NULL DEFAULT 85,
                        `interviewAvailable` INTEGER NOT NULL DEFAULT 1,
                        `status` TEXT NOT NULL DEFAULT 'Active Student Profile',
                        `createdAt` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `executive_memories` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `category` TEXT NOT NULL DEFAULT 'Partnership',
                        `institutionName` TEXT NOT NULL DEFAULT '',
                        `details` TEXT NOT NULL DEFAULT '',
                        `status` TEXT NOT NULL DEFAULT 'Completed',
                        `priority` TEXT NOT NULL DEFAULT 'High',
                        `firstContactDate` TEXT NOT NULL DEFAULT '2026-07-01',
                        `lastContactDate` TEXT NOT NULL DEFAULT '2026-08-05',
                        `lastEmail` TEXT NOT NULL DEFAULT '',
                        `meetingNotes` TEXT NOT NULL DEFAULT '',
                        `scholarshipInfo` TEXT NOT NULL DEFAULT '',
                        `englishReq` TEXT NOT NULL DEFAULT '',
                        `moiPolicy` TEXT NOT NULL DEFAULT '',
                        `interviewPolicy` TEXT NOT NULL DEFAULT '',
                        `agentStatus` TEXT NOT NULL DEFAULT 'Active Partner',
                        `priorityScore` INTEGER NOT NULL DEFAULT 92,
                        `riskScore` INTEGER NOT NULL DEFAULT 10,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crm_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
