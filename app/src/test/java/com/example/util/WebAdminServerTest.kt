package com.example.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ContactLog
import com.example.data.MeetingRecord
import com.example.data.University
import com.example.data.UniversityDao
import com.example.data.UniversityNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.ServerSocket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WebAdminServerTest {

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun teardown() {
        WebAdminServer.stopServer { _, _ -> }
        Dispatchers.resetMain()
    }

    @Test
    fun `startServer handles port in use error`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dummyDao = object : UniversityDao {
            override fun getAllActive(): Flow<List<University>> = emptyFlow()
            override suspend fun getRawActiveList(): List<University> = emptyList()
            override fun getAll(): Flow<List<University>> = emptyFlow()
            override suspend fun getById(id: Int): University? = null
            override suspend fun findByNameOrEmail(name: String, email: String): University? = null
            override suspend fun insert(university: University): Long = 0
            override suspend fun update(university: University) {}
            override suspend fun delete(university: University) {}
            override fun getContactLogs(universityId: Int): Flow<List<ContactLog>> = emptyFlow()
            override suspend fun insertContactLog(log: ContactLog) {}
            override fun getNotes(universityId: Int): Flow<List<UniversityNote>> = emptyFlow()
            override suspend fun insertNote(note: UniversityNote) {}
            override suspend fun deleteNote(note: UniversityNote) {}
            override fun getMeetings(universityId: Int): Flow<List<MeetingRecord>> = emptyFlow()
            override fun getAllMeetings(): Flow<List<MeetingRecord>> = emptyFlow()
            override suspend fun insertMeeting(meeting: MeetingRecord) {}
            override suspend fun deleteMeeting(meeting: MeetingRecord) {}
        }

        // Bind the port before starting the server
        val blockingSocket = ServerSocket(WebAdminServer.PORT)

        try {
            var errorEmitted = false
            val latch = CountDownLatch(1)

            WebAdminServer.startServer(context, dummyDao) { isRunning, message ->
                if (!isRunning && (message.contains("Server Error") || message.contains("Server stopped"))) {
                    errorEmitted = true
                    latch.countDown()
                }
            }

            // Wait for the coroutine to process the exception and call the callback
            latch.await(5, TimeUnit.SECONDS)

            assertTrue("Expected startServer to emit error due to port in use", errorEmitted)
        } finally {
            blockingSocket.close()
        }
    }
}
