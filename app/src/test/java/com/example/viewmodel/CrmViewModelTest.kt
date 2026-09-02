package com.example.viewmodel

import android.app.Application
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CrmViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var application: Application
    private lateinit var dao: UniversityDao
    private lateinit var todoDao: TodoDao

    private lateinit var viewModel: CrmViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        application = RuntimeEnvironment.getApplication()

        // Simple manual mock implementations
        dao = object : UniversityDao {
            override fun getAllActive() = flowOf(emptyList<University>())
            override suspend fun getRawActiveList() = emptyList<University>()
            override fun getAll() = flowOf(emptyList<University>())
            override suspend fun getById(id: Int) = null
            override suspend fun findByNameOrEmail(name: String, email: String) = null
            override suspend fun insert(university: University) = 1L
            override suspend fun update(university: University) {}
            override suspend fun delete(university: University) {}
            override fun getContactLogs(universityId: Int) = flowOf(emptyList<ContactLog>())
            override suspend fun insertContactLog(log: ContactLog) {}
            override fun getNotes(universityId: Int) = flowOf(emptyList<UniversityNote>())
            override suspend fun insertNote(note: UniversityNote) {}
            override suspend fun deleteNote(note: UniversityNote) {}
            override fun getMeetings(universityId: Int) = flowOf(emptyList<MeetingRecord>())
            override fun getAllMeetings() = flowOf(emptyList<MeetingRecord>())
            override suspend fun insertMeeting(meeting: MeetingRecord) {}
            override suspend fun deleteMeeting(meeting: MeetingRecord) {}
        }

        todoDao = object : TodoDao {
            override fun getAllTodos() = flowOf(emptyList<TodoItem>())
            override fun getTodosForUniversity(universityId: Int) = flowOf(emptyList<TodoItem>())
            override suspend fun insert(todoItem: TodoItem) {}
            override suspend fun update(todoItem: TodoItem) {}
            override suspend fun delete(todoItem: TodoItem) {}
        }

        viewModel = CrmViewModel(
            application = application,
            dao = dao,
            todoDao = todoDao,
            agentDao = null,
            securityManager = null,
            reminderDao = null,
            researchDao = null,
            studentDao = null,
            executiveMemoryDao = null
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test setSearchQuery updates search query state`() = runTest {
        viewModel.setSearchQuery("test query")
        assertEquals("test query", viewModel.searchQuery.value)
    }

    @Test
    fun `test setStatusFilter updates status filter state`() = runTest {
        viewModel.setStatusFilter("Prospect")
        assertEquals("Prospect", viewModel.statusFilter.value)
    }

    @Test
    fun `test setCountryFilter updates country filter state`() = runTest {
        viewModel.setCountryFilter("United Kingdom")
        assertEquals("United Kingdom", viewModel.countryFilter.value)
    }

    @Test
    fun `test setPriorityFilter updates priority filter state`() = runTest {
        viewModel.setPriorityFilter("High")
        assertEquals("High", viewModel.priorityFilter.value)
    }
}
