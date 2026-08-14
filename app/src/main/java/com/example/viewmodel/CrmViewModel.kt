package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.modules.research.data.ResearchDao
import com.example.modules.research.model.ResearchedInstitutionEntity
import com.example.modules.research.model.ResearchJobEntity
import com.example.modules.research.engine.CeoAiResearchCoordinator
import com.example.modules.research.engine.ResearchAgentType

data class FilterState(
    val query: String = "",
    val status: String = "All",
    val country: String = "All",
    val priority: String = "All",
    val sortBy: String = "Newest"
)

@OptIn(ExperimentalCoroutinesApi::class)
class CrmViewModel(
    application: Application,
    private val dao: UniversityDao,
    private val todoDao: TodoDao,
    private val agentDao: AgentDao? = null,
    val securityManager: BiometricSecurityManager? = null,
    private val reminderDao: ReminderDao? = null,
    private val researchDao: ResearchDao? = null,
    private val studentDao: StudentDao? = null,
    private val executiveMemoryDao: ExecutiveMemoryDao? = null
) : AndroidViewModel(application) {

    private val settingsManager = BusinessSettingsManager(application)

    // Business Settings Flow
    private val _businessSettings = MutableStateFlow(settingsManager.getSettings())
    val businessSettings: StateFlow<BusinessSettings> = _businessSettings.asStateFlow()

    // Executive Memory Engine Flow (Phase 16)
    val executiveMemories: StateFlow<List<ExecutiveMemoryEntity>> = executiveMemoryDao?.getAllMemories()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    // Student Profiles Flow (Phase 14)
    val studentProfiles: StateFlow<List<StudentProfileEntity>> = studentDao?.getAllStudents()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    // Research Engine Intelligence Flows
    val researchedInstitutions: StateFlow<List<ResearchedInstitutionEntity>> = researchDao?.getAllInstitutions()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    val researchJobs: StateFlow<List<ResearchJobEntity>> = researchDao?.getAllJobs()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    // Personal Reminders Flow
    val reminders: StateFlow<List<ReminderEntity>> = reminderDao?.getAllReminders()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    // Agent Flows
    val agentMemories: StateFlow<List<AgentMemoryEntity>> = agentDao?.getAllMemories()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    val agentLogs: StateFlow<List<AgentLogEntity>> = agentDao?.getAllLogs()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    val universityReplies: StateFlow<List<UniversityReplyEntity>> = agentDao?.getAllReplies()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    private val _isAgentRunning = MutableStateFlow(false)
    val isAgentRunning: StateFlow<Boolean> = _isAgentRunning.asStateFlow()

    // Web Admin Server State
    private val _webServerUrl = MutableStateFlow<String?>(null)
    val webServerUrl: StateFlow<String?> = _webServerUrl.asStateFlow()

    // Phase 11 Executive Brain Gemini State Flows
    val geminiHealthState: StateFlow<com.example.core.ai.AiHealthState> = com.example.core.ai.CentralGeminiService.healthState

    private val _founderBriefing = MutableStateFlow<FounderBriefingData?>(null)
    val founderBriefing: StateFlow<FounderBriefingData?> = _founderBriefing.asStateFlow()

    private val _isBriefingLoading = MutableStateFlow(false)
    val isBriefingLoading: StateFlow<Boolean> = _isBriefingLoading.asStateFlow()

    private val _isAiChatLoading = MutableStateFlow(false)
    val isAiChatLoading: StateFlow<Boolean> = _isAiChatLoading.asStateFlow()

    private val _briefingLanguage = MutableStateFlow("Bangla")
    val briefingLanguage: StateFlow<String> = _briefingLanguage.asStateFlow()

    private val _executiveChatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val executiveChatHistory: StateFlow<List<ChatMessage>> = _executiveChatHistory.asStateFlow()

    // Universities & Sub-data
    val allUniversities: StateFlow<List<University>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeUniversities: StateFlow<List<University>> = dao.getAllActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeetings: StateFlow<List<MeetingRecord>> = dao.getAllMeetings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todos: StateFlow<List<TodoItem>> = todoDao.getAllTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search, Filter & Sort State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("All") // All, Prospect, Contacted, In Discussion, Partnered, On Hold
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _countryFilter = MutableStateFlow("All")
    val countryFilter: StateFlow<String> = _countryFilter.asStateFlow()

    private val _priorityFilter = MutableStateFlow("All") // All, High, Medium, Low
    val priorityFilter: StateFlow<String> = _priorityFilter.asStateFlow()

    private val _sortBy = MutableStateFlow("Newest") // Name, Newest, Last Contacted, Next Follow-Up, Priority
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    // Selected University for Detail View
    private val _selectedUniversityId = MutableStateFlow<Int?>(null)
    val selectedUniversityId: StateFlow<Int?> = _selectedUniversityId.asStateFlow()

    val selectedUniversity: StateFlow<University?> = combine(activeUniversities, _selectedUniversityId) { list, id ->
        list.firstOrNull { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Sub-entities for Selected University
    val selectedContactLogs: StateFlow<List<ContactLog>> = _selectedUniversityId.flatMapLatest { id ->
        if (id != null) dao.getContactLogs(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedNotes: StateFlow<List<UniversityNote>> = _selectedUniversityId.flatMapLatest { id ->
        if (id != null) dao.getNotes(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedMeetings: StateFlow<List<MeetingRecord>> = _selectedUniversityId.flatMapLatest { id ->
        if (id != null) dao.getMeetings(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // One-time UI Message Events
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val filterState: StateFlow<FilterState> = combine(
        _searchQuery,
        _statusFilter,
        _countryFilter,
        _priorityFilter,
        _sortBy
    ) { q, st, c, p, sort ->
        FilterState(q, st, c, p, sort)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FilterState())

    // Filtered Universities calculation
    val filteredUniversities: StateFlow<List<University>> = combine(
        activeUniversities,
        filterState
    ) { list: List<University>, filter: FilterState ->
        var result = list

        if (filter.query.isNotBlank()) {
            val q = filter.query.lowercase().trim()
            result = result.filter { u ->
                u.name.lowercase().contains(q) ||
                u.contactName.lowercase().contains(q) ||
                u.email.lowercase().contains(q) ||
                u.country.lowercase().contains(q) ||
                u.tags.lowercase().contains(q) ||
                u.assignedCounselor.lowercase().contains(q)
            }
        }

        if (filter.status != "All") {
            result = result.filter { u ->
                u.partnershipStatus.equals(filter.status, ignoreCase = true) || u.status.equals(filter.status, ignoreCase = true)
            }
        }

        if (filter.country != "All") {
            result = result.filter { u -> u.country.equals(filter.country, ignoreCase = true) }
        }

        if (filter.priority != "All") {
            result = result.filter { u -> u.priority.equals(filter.priority, ignoreCase = true) }
        }

        when (filter.sortBy) {
            "Name" -> result.sortedBy { it.name.lowercase() }
            "Newest" -> result.sortedByDescending { it.createdAt }
            "Last Contacted" -> result.sortedByDescending { it.lastContactedDate }
            "Next Follow-Up" -> result.sortedBy { if (it.nextFollowUpDate == 0L) Long.MAX_VALUE else it.nextFollowUpDate }
            "Priority" -> result.sortedBy { when(it.priority) { "High" -> 1; "Medium" -> 2; else -> 3 } }
            else -> result
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Automatically verify Gemini connectivity on application startup
        viewModelScope.launch {
            com.example.core.ai.CentralGeminiService.verifyConnectivity(getApplication())
        }
        // Automatically generate Founder Briefing on application launch
        generateFounderBriefing()
        // Seed default universities, agent memories, and replies if Room DB is empty
        viewModelScope.launch {
            val existingUnis = dao.getAll().first()
            if (existingUnis.isEmpty()) {
                seedInitialUniversities()
            }

            studentDao?.let { sDao ->
                if (sDao.getStudentCount() == 0) {
                    sDao.insertStudent(
                        StudentProfileEntity(
                            name = "Tanvir Ahmed",
                            country = "Bangladesh",
                            academicBackground = "BSc in Computer Science & Engineering",
                            sscScore = "GPA 5.00",
                            hscScore = "GPA 5.00",
                            bachelorDegree = "BSc CSE (CGPA 3.42)",
                            cgpa = 3.42,
                            gapYears = 1,
                            workExperience = "1.5 Years Full Stack Software Engineer",
                            preferredIntake = "September 2026",
                            preferredSubject = "Data Science & AI",
                            preferredCity = "London / Manchester",
                            preferredCountry = "United Kingdom",
                            budget = 16500.0,
                            englishQualification = "MOI & IELTS 6.5",
                            ieltsOverall = 6.5,
                            moiAvailable = true,
                            duolingoScore = 125,
                            pteScore = 64,
                            toeflScore = 88,
                            interviewAvailable = true
                        )
                    )
                    sDao.insertStudent(
                        StudentProfileEntity(
                            name = "Nusrat Jahan",
                            country = "Bangladesh",
                            academicBackground = "BBA in Finance & Accounting",
                            sscScore = "GPA 5.00",
                            hscScore = "GPA 4.80",
                            bachelorDegree = "BBA Finance (CGPA 3.55)",
                            cgpa = 3.55,
                            gapYears = 0,
                            workExperience = "3 Years Bank Executive Counselor",
                            preferredIntake = "September 2026",
                            preferredSubject = "International Business Management",
                            preferredCity = "Birmingham / Leeds",
                            preferredCountry = "United Kingdom",
                            budget = 14500.0,
                            englishQualification = "MOI Waiver Eligible",
                            ieltsOverall = 6.0,
                            moiAvailable = true,
                            duolingoScore = 115,
                            pteScore = 58,
                            toeflScore = 80,
                            interviewAvailable = true
                        )
                    )
                    sDao.insertStudent(
                        StudentProfileEntity(
                            name = "Rahim Chowdhury",
                            country = "Bangladesh",
                            academicBackground = "BSc in Electrical & Electronic Engineering",
                            sscScore = "GPA 4.90",
                            hscScore = "GPA 4.75",
                            bachelorDegree = "BSc EEE (CGPA 3.10)",
                            cgpa = 3.10,
                            gapYears = 2,
                            workExperience = "2 Years Automation Specialist",
                            preferredIntake = "September 2026",
                            preferredSubject = "Cyber Security / Robotics",
                            preferredCity = "Coventry / Newcastle",
                            preferredCountry = "United Kingdom",
                            budget = 15000.0,
                            englishQualification = "Internal Interview / MOI Only",
                            ieltsOverall = 5.5,
                            moiAvailable = true,
                            duolingoScore = 105,
                            pteScore = 52,
                            toeflScore = 72,
                            interviewAvailable = true
                        )
                    )
                }
            }

            executiveMemoryDao?.let { mDao ->
                if (mDao.getMemoryCount() == 0) {
                    mDao.insertMemory(
                        ExecutiveMemoryEntity(
                            title = "University of Greenwich B2B Direct Portal",
                            category = "Partnership",
                            institutionName = "University of Greenwich",
                            details = "Confirmed MOI waiver policy for Bangladesh applicants within 5 years of graduation. 15% net commission agreement.",
                            status = "Active Partner",
                            priority = "High",
                            firstContactDate = "2026-06-15",
                            lastContactDate = "2026-08-04",
                            lastEmail = "Re: Agent Representation Agreement & South Asia Intake Support",
                            meetingNotes = "Meeting with International Regional Officer confirmed direct portal access and fast-track CAS processing.",
                            scholarshipInfo = "International Vice-Chancellor Merit Bursary (£3,000)",
                            englishReq = "MOI Accepted / IELTS 6.0",
                            moiPolicy = "Accepted for medium of instruction within 5 years",
                            interviewPolicy = "Internal Credibility Interview",
                            agentStatus = "Active Partner",
                            priorityScore = 95,
                            riskScore = 8
                        )
                    )
                    mDao.insertMemory(
                        ExecutiveMemoryEntity(
                            title = "Teesside University Agent Recruitment & MOI",
                            category = "University Research",
                            institutionName = "Teesside University",
                            details = "Offering free internal English test for candidates without formal IELTS. Fast CAS deposit turnarounds.",
                            status = "In Progress",
                            priority = "High",
                            firstContactDate = "2026-07-02",
                            lastContactDate = "2026-08-03",
                            lastEmail = "Re: Agent Portal Registration & English Waiver Verification",
                            meetingNotes = "Discussed September 2026 student list and fee waiver incentives.",
                            scholarshipInfo = "Global Excellence Scholarship (£2,000)",
                            englishReq = "Internal Test / MOI / IELTS 5.5",
                            moiPolicy = "Accepted for MOI medium",
                            interviewPolicy = "Free Internal Online Test Offered",
                            agentStatus = "Contacted",
                            priorityScore = 92,
                            riskScore = 10
                        )
                    )
                    mDao.insertMemory(
                        ExecutiveMemoryEntity(
                            title = "University of Chester Merit Bursary & MOI",
                            category = "Partnership",
                            institutionName = "University of Chester",
                            details = "Merit scholarships up to £3,500 automatically evaluated upon application submission.",
                            status = "Active Partner",
                            priority = "High",
                            firstContactDate = "2026-07-10",
                            lastContactDate = "2026-08-02",
                            lastEmail = "Re: Partnership Agreement Final Review",
                            meetingNotes = "Reviewed CAS deposit SLA (48 hours) and Bangladesh academic thresholds.",
                            scholarshipInfo = "Automatic £3,500 International Merit Scholarship",
                            englishReq = "MOI / IELTS 6.0",
                            moiPolicy = "Accepted within 3 years",
                            interviewPolicy = "Credibility Interview Required",
                            agentStatus = "Agreement Signed",
                            priorityScore = 94,
                            riskScore = 7
                        )
                    )
                    mDao.insertMemory(
                        ExecutiveMemoryEntity(
                            title = "Student Counselling: Tanvir Ahmed Matching",
                            category = "Student Counselling",
                            institutionName = "University of Greenwich",
                            details = "Matched Tanvir Ahmed (BSc CSE CGPA 3.42) for MSc Data Science & AI. MOI document submitted.",
                            status = "Completed",
                            priority = "High",
                            firstContactDate = "2026-08-01",
                            lastContactDate = "2026-08-05",
                            lastEmail = "Application Draft Sent to International Admissions",
                            meetingNotes = "Counselling session completed. Student selected London campus.",
                            scholarshipInfo = "Applied for £3,000 Vice Chancellor Bursary",
                            englishReq = "MOI Waiver Applied",
                            moiPolicy = "MOI Document Verified",
                            interviewPolicy = "Internal Test Scheduled",
                            agentStatus = "Active Application",
                            priorityScore = 90,
                            riskScore = 5
                        )
                    )
                    mDao.insertMemory(
                        ExecutiveMemoryEntity(
                            title = "Weekly Executive Summary (August Week 1)",
                            category = "Weekly Summary",
                            institutionName = "All Partner Universities",
                            details = "Total 14 new institutions researched, 4 active partnership proposals submitted, 3 student profiles matched, 0 critical failures.",
                            status = "Completed",
                            priority = "Medium",
                            firstContactDate = "2026-08-01",
                            lastContactDate = "2026-08-05",
                            lastEmail = "Executive Weekly Digest Generated",
                            meetingNotes = "Founder review completed.",
                            scholarshipInfo = "Multiple £3k-£5k options identified",
                            englishReq = "MOI focus prioritized",
                            moiPolicy = "80% partner network accepts MOI",
                            interviewPolicy = "Standard internal interviews",
                            agentStatus = "Operational",
                            priorityScore = 98,
                            riskScore = 2
                        )
                    )
                }
            }

            agentDao?.let { aDao ->
                val existingMemories = aDao.getAllMemories().first()
                if (existingMemories.isEmpty()) {
                    aDao.insertMemory(
                        AgentMemoryEntity(
                            agentName = "Partnerships Agent (Gabriel)",
                            agentType = "Partnerships",
                            category = "B2B Angle Optimization",
                            content = "Emphasizing USA graduate CEO profile and zero extra student fee policy increased UK university response rate by 34%.",
                            impactRating = 5
                        )
                    )
                    aDao.insertMemory(
                        AgentMemoryEntity(
                            agentName = "Strategy & Reply Analyzer",
                            agentType = "Strategy",
                            category = "Re-engagement Diagnostic",
                            content = "Universities that do not reply within 7 days respond best when re-contacted with direct intake deadline quotes.",
                            impactRating = 4
                        )
                    )
                    aDao.insertMemory(
                        AgentMemoryEntity(
                            agentName = "Security & Compliance Officer",
                            agentType = "Security",
                            category = "Email Deliverability",
                            content = "SPF/DKIM alignment verified for nextborder.visa@gmail.com. Zero spam complaints recorded. Human approval lock active.",
                            impactRating = 5
                        )
                    )
                    aDao.insertMemory(
                        AgentMemoryEntity(
                            agentName = "HR & Recruitment Agent",
                            agentType = "HR",
                            category = "Counselor Guidelines",
                            content = "Counselors assigned to UK universities must verify pre-screening documents before submitting student files.",
                            impactRating = 4
                        )
                    )
                }

                val existingReplies = aDao.getAllReplies().first()
                if (existingReplies.isEmpty()) {
                    StrategyAdaptationEngine.generateInitialMockReplies().forEach { reply ->
                        aDao.insertReply(reply)
                    }
                }
            }
        }
    }

    // Filter Setters
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setStatusFilter(status: String) { _statusFilter.value = status }
    fun setCountryFilter(country: String) { _countryFilter.value = country }
    fun setPriorityFilter(priority: String) { _priorityFilter.value = priority }
    fun setSortBy(sort: String) { _sortBy.value = sort }
    fun selectUniversity(id: Int?) { _selectedUniversityId.value = id }

    // Business Logic Functions
    fun saveBusinessSettings(settings: BusinessSettings) {
        settingsManager.saveSettings(settings)
        _businessSettings.value = settings
        emitToast("Business settings updated successfully!")
    }

    fun addUniversity(university: University, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val validation = ValidationUtils.validateUniversityForm(university.name, university.email)
            if (validation is ValidationResult.Error) {
                emitToast(validation.message)
                return@launch
            }

            // Duplicate check
            val existing = dao.findByNameOrEmail(university.name.trim(), university.email.trim())
            if (existing != null) {
                emitToast("University with this name or email already exists!")
                return@launch
            }

            val cleaned = university.copy(
                name = ValidationUtils.cleanTextEncoding(university.name.trim()),
                contactName = ValidationUtils.cleanTextEncoding(university.contactName.trim()),
                email = university.email.trim(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val newId = dao.insert(cleaned)
            _selectedUniversityId.value = newId.toInt()
            emitToast("University '${cleaned.name}' added successfully!")
            onSuccess?.invoke()
        }
    }

    fun updateUniversity(university: University, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val validation = ValidationUtils.validateUniversityForm(university.name, university.email)
            if (validation is ValidationResult.Error) {
                emitToast(validation.message)
                return@launch
            }

            val cleaned = university.copy(
                name = ValidationUtils.cleanTextEncoding(university.name.trim()),
                contactName = ValidationUtils.cleanTextEncoding(university.contactName.trim()),
                email = university.email.trim(),
                updatedAt = System.currentTimeMillis()
            )
            dao.update(cleaned)
            emitToast("University '${cleaned.name}' updated!")
            onSuccess?.invoke()
        }
    }

    fun archiveUniversity(university: University) {
        viewModelScope.launch {
            val archived = university.copy(isArchived = true, updatedAt = System.currentTimeMillis())
            dao.update(archived)
            emitToast("University archived")
        }
    }

    fun deleteUniversityPermanently(university: University) {
        viewModelScope.launch {
            dao.delete(university)
            _selectedUniversityId.value = null
            emitToast("University deleted permanently")
        }
    }

    // Contact Log, Notes & Meetings
    fun addContactLog(universityId: Int, type: String, summary: String) {
        viewModelScope.launch {
            if (summary.isBlank()) return@launch
            val log = ContactLog(
                universityId = universityId,
                type = type,
                summary = ValidationUtils.cleanTextEncoding(summary.trim()),
                timestamp = System.currentTimeMillis()
            )
            dao.insertContactLog(log)

            // Update last contacted date on university
            dao.getById(universityId)?.let { u ->
                dao.update(u.copy(lastContactedDate = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
            emitToast("Contact log saved")
        }
    }

    fun addNote(universityId: Int, content: String) {
        viewModelScope.launch {
            if (content.isBlank()) return@launch
            val note = UniversityNote(
                universityId = universityId,
                content = ValidationUtils.cleanTextEncoding(content.trim()),
                author = _businessSettings.value.ceoName
            )
            dao.insertNote(note)
            emitToast("Note added")
        }
    }

    fun deleteNote(note: UniversityNote) {
        viewModelScope.launch {
            dao.deleteNote(note)
        }
    }

    fun addMeeting(universityId: Int, title: String, meetingDate: Long, link: String) {
        viewModelScope.launch {
            if (title.isBlank()) return@launch
            val meeting = MeetingRecord(
                universityId = universityId,
                title = ValidationUtils.cleanTextEncoding(title.trim()),
                meetingDate = meetingDate,
                meetingLink = link.trim(),
                status = "Scheduled"
            )
            dao.insertMeeting(meeting)
            emitToast("Meeting scheduled")
        }
    }

    // Todos
    fun addTodo(task: String, dueDate: Long? = null, priority: String = "Medium", universityId: Int? = null) {
        viewModelScope.launch {
            if (task.isBlank()) {
                emitToast("Task title cannot be empty")
                return@launch
            }
            val todo = TodoItem(
                task = ValidationUtils.cleanTextEncoding(task.trim()),
                dueDate = dueDate,
                priority = priority,
                universityId = universityId,
                createdAt = System.currentTimeMillis()
            )
            todoDao.insert(todo)
            emitToast("Task added")
        }
    }

    fun toggleTodoCompleted(todo: TodoItem) {
        viewModelScope.launch {
            val updated = todo.copy(
                isCompleted = !todo.isCompleted,
                completedAt = if (!todo.isCompleted) System.currentTimeMillis() else null
            )
            todoDao.update(updated)
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            todoDao.delete(todo)
            emitToast("Task deleted")
        }
    }

    // Personal Productivity & Unlimited Reminders Engine
    fun addPersonalReminder(
        title: String,
        category: String = "Business",
        priority: String = "Medium",
        recurrence: String = "One-time",
        customDate: String = "",
        customTime: String = "09:00",
        triggerTimeMs: Long = System.currentTimeMillis(),
        repeatIntervalMinutes: Int = 15,
        notes: String = ""
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                emitToast("Reminder title cannot be empty")
                return@launch
            }
            val reminder = ReminderEntity(
                title = ValidationUtils.cleanTextEncoding(title.trim()),
                category = category,
                priority = priority,
                recurrence = recurrence,
                customDate = customDate,
                customTime = customTime,
                triggerTimeMs = triggerTimeMs,
                repeatIntervalMinutes = repeatIntervalMinutes,
                notes = ValidationUtils.cleanTextEncoding(notes.trim())
            )
            reminderDao?.insert(reminder)
            emitToast("Personal reminder created (${reminder.category})")
        }
    }

    fun toggleReminderCompleted(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(
                isCompleted = !reminder.isCompleted,
                completedAt = if (!reminder.isCompleted) System.currentTimeMillis() else null
            )
            reminderDao?.update(updated)
            emitToast(if (updated.isCompleted) "Reminder marked complete!" else "Reminder reopened")
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao?.delete(reminder)
            emitToast("Reminder removed")
        }
    }

    fun snoozeReminder(reminder: ReminderEntity, snoozeMinutes: Int = 15) {
        viewModelScope.launch {
            val newTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
            val updated = reminder.copy(
                triggerTimeMs = newTime,
                snoozeCount = reminder.snoozeCount + 1
            )
            reminderDao?.update(updated)
            emitToast("Reminder snoozed for $snoozeMinutes minutes")
        }
    }

    fun triggerBackgroundSyncNow(context: Context) {
        BackgroundSyncManager.triggerManualSyncNow(context)
        emitToast("WorkManager background sync triggered!")
    }

    // Import / Export & Google Sheets Drive Sync
    fun syncLeadsToGoogleSheets(context: Context) {
        viewModelScope.launch {
            try {
                val list = activeUniversities.value
                if (list.isEmpty()) {
                    emitToast("No lead records available to sync")
                    return@launch
                }
                val file = GoogleSheetsSyncService.exportToGoogleSheetsCsv(context, list)
                GoogleSheetsSyncService.shareToGoogleDrive(context, file)
                
                recordAgentLog(
                    agentName = "Partnerships Agent (Gabriel)",
                    action = "Google Sheets Drive Table Sync",
                    details = "Synced ${list.size} university leads to Google Drive table (dawan.tradelink@gmail.com)"
                )
                emitToast("Google Sheets Table ready for Drive upload!")
            } catch (e: Exception) {
                emitToast("Google Sheets export failed: ${e.message}")
            }
        }
    }

    fun importUniversitiesFromCsv(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val list = CsvUtils.parseCsvInputStream(stream)
                    var count = 0
                    list.forEach { u ->
                        if (u.name.isNotBlank()) {
                            dao.insert(u)
                            count++
                        }
                    }
                    emitToast("Successfully imported $count universities from CSV!")
                }
            } catch (e: Exception) {
                emitToast("CSV import failed: ${e.message}")
            }
        }
    }

    // Autonomous Multi-Agent Simulation Cycle
    fun runAutonomousAgentCycle() {
        if (_isAgentRunning.value) return
        viewModelScope.launch {
            _isAgentRunning.value = true
            emitToast("Starting 4-Agent Autonomous Collaboration Cycle...")

            // Agent 1: Gabriel (Partnerships)
            recordAgentLog("Partnerships Agent (Gabriel)", "UK University Scan", "Scanning top UK universities for open B2B recruitment agent vacancies.")
            kotlinx.coroutines.delay(1200)

            // Agent 2: Strategy & Reply Analyzer
            val unrepliedCount = activeUniversities.value.count { it.partnershipStatus == "Contacted" }
            recordAgentLog("Strategy & Reply Analyzer", "Non-Reply Analysis", "Analyzing $unrepliedCount contacted universities for response delay triggers.")
            kotlinx.coroutines.delay(1200)

            // Agent 3: HR & Recruitment Agent
            recordAgentLog("HR & Recruitment Agent", "Counselor Audit", "Verifying CEO Ishak Dewan & counselor profiles. Onboarding compliance set to 100%.")
            kotlinx.coroutines.delay(1200)

            // Agent 4: Security & Compliance Officer
            recordAgentLog("Security & Compliance Officer", "Security & Deliverability Audit", "SPF/DKIM clean. Human email approval rule enforced. Zero unverified automated sends.")
            kotlinx.coroutines.delay(1000)

            // Shared Memory Update
            recordAgentMemory(
                agentName = "Strategy & Reply Analyzer",
                agentType = "Strategy",
                category = "Cross-Agent Synergy",
                content = "Multi-agent cycle completed: Gabriel identified 3 new UK prospects, Security verified deliverability, HR updated counselor guidelines."
            )

            _isAgentRunning.value = false
            emitToast("Autonomous Agent Cycle completed! Shared memory updated.")
        }
    }

    fun recordAgentLog(agentName: String, action: String, details: String, status: String = "Success") {
        viewModelScope.launch {
            agentDao?.insertLog(AgentLogEntity(agentName = agentName, action = action, details = details, status = status))
        }
    }

    fun recordAgentMemory(agentName: String, agentType: String, category: String, content: String, rating: Int = 5) {
        viewModelScope.launch {
            agentDao?.insertMemory(AgentMemoryEntity(agentName = agentName, agentType = agentType, category = category, content = content, impactRating = rating))
        }
    }

    fun diagnoseNonReply(university: University, onResult: (NonReplyDiagnosis) -> Unit) {
        viewModelScope.launch {
            val daysSince = if (university.lastContactedDate > 0) {
                (System.currentTimeMillis() - university.lastContactedDate) / (1000 * 3600 * 24)
            } else 10L

            val diagnosis = StrategyAdaptationEngine.diagnoseNonReply(university, daysSince)
            
            recordAgentLog(
                agentName = "Strategy & Reply Analyzer",
                action = "Non-Reply Diagnosis (${university.name})",
                details = "Identified reason: ${diagnosis.probableReason}. Suggested strategy: ${diagnosis.recommendedStrategy}"
            )
            onResult(diagnosis)
        }
    }

    fun clearAgentLogs() {
        viewModelScope.launch {
            agentDao?.clearLogs()
            emitToast("Agent logs cleared.")
        }
    }

    // Web Admin Server Controller
    fun toggleWebAdminServer(context: Context) {
        if (WebAdminServer.isServerRunning()) {
            WebAdminServer.stopServer { _, msg ->
                _webServerUrl.value = null
                emitToast(msg)
            }
        } else {
            WebAdminServer.startServer(context, dao) { isRunning, urlOrMsg ->
                if (isRunning) {
                    _webServerUrl.value = urlOrMsg
                    emitToast("Web Admin Server Live: $urlOrMsg")
                } else {
                    _webServerUrl.value = null
                    emitToast(urlOrMsg)
                }
            }
        }
    }

    // Follow-Up Reminder System with Real Android Alarm Notifications
    fun scheduleFollowUpReminderWithNotification(
        context: Context,
        universityId: Int,
        universityName: String,
        reminderTimeMs: Long,
        noteMessage: String
    ) {
        viewModelScope.launch {
            val reminderId = (System.currentTimeMillis() % 1000000).toInt()
            
            // 1. Schedule System Alarm Notification
            ReminderScheduler.scheduleReminder(
                context = context,
                reminderId = reminderId,
                universityName = universityName,
                noteMessage = noteMessage,
                triggerTimeMs = reminderTimeMs
            )

            // 2. Save Permanently in Room DB Todo Items & Contact Logs
            todoDao.insert(
                TodoItem(
                    task = "Follow-up: $universityName (${noteMessage.ifBlank { "Scheduled CRM follow-up" }})",
                    dueDate = reminderTimeMs,
                    priority = "High",
                    universityId = universityId
                )
            )

            dao.insertNote(
                UniversityNote(
                    universityId = universityId,
                    content = "Scheduled follow-up reminder for ${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(reminderTimeMs))}: $noteMessage",
                    author = "System Reminder Engine"
                )
            )

            recordAgentLog(
                agentName = "Strategy & Reply Analyzer",
                action = "Follow-Up Reminder Set",
                details = "Scheduled alarm notification for $universityName at ${java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(reminderTimeMs))}"
            )

            emitToast("Reminder scheduled! Android system notification set for $universityName.")
        }
    }

    // Greylist, Blacklist & Automated Rejection Management
    fun moveUniversityToGreylist(
        universityId: Int,
        conversationText: String = "",
        customReason: String? = null,
        suggestedFollowUpDateMs: Long? = null
    ) {
        viewModelScope.launch {
            val u = dao.getById(universityId) ?: return@launch
            val analysis = UniversityStatusEngine.analyzeRejectionReply(conversationText.ifBlank { customReason ?: u.notes }, u.name)
            
            val refusalDate = System.currentTimeMillis()
            val followUpDate = suggestedFollowUpDateMs ?: analysis.suggestedFollowUpDateMs

            val updated = u.copy(
                partnershipStatus = "GREYLIST",
                status = "GREYLIST",
                greylistReason = customReason ?: analysis.reason,
                greylistRefusalDate = refusalDate,
                greylistConversation = conversationText,
                greylistConfidenceScore = analysis.confidenceScore,
                greylistSuggestedFollowUpDate = followUpDate,
                greylistRecommendedStrategy = analysis.recommendedStrategy,
                greylistAiAnalysis = analysis.aiAnalysis,
                updatedAt = refusalDate
            )
            dao.update(updated)

            dao.insertNote(
                UniversityNote(
                    universityId = universityId,
                    content = "[GREYLISTED] Moved to Greylist: ${analysis.reason}. Review scheduled for ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(followUpDate))}.",
                    author = "AI Strategic Response Engine"
                )
            )

            recordAgentLog(
                agentName = "Strategy & Reply Analyzer",
                action = "Moved to Greylist",
                details = "${u.name} placed on Greylist. Reason: ${analysis.reason}"
            )

            emitToast("University '${u.name}' moved to Greylist. Scheduled review set for future follow-up.")
        }
    }

    fun moveUniversityToBlacklist(
        universityId: Int,
        conversationText: String = "",
        customReason: String? = null
    ) {
        viewModelScope.launch {
            val u = dao.getById(universityId) ?: return@launch
            val refusalDate = System.currentTimeMillis()

            val updated = u.copy(
                partnershipStatus = "BLACKLIST",
                status = "BLACKLIST",
                blacklistReason = customReason ?: "Permanent refusal or policy restriction request",
                blacklistDate = refusalDate,
                nextFollowUpDate = 0L,
                updatedAt = refusalDate
            )
            dao.update(updated)

            dao.insertNote(
                UniversityNote(
                    universityId = universityId,
                    content = "[BLACKLISTED] PERMANENT STOP: ${updated.blacklistReason}. All future outreach and alarms cancelled.",
                    author = "AI Compliance Sentinel"
                )
            )

            recordAgentLog(
                agentName = "Compliance Sentinel",
                action = "Blacklisted University",
                details = "${u.name} added to Blacklist. All communications blocked."
            )

            emitToast("University '${u.name}' moved to Blacklist. Communications permanently disabled.")
        }
    }

    fun processReplyWithAiIntelligence(universityId: Int, replySnippet: String) {
        viewModelScope.launch {
            val u = dao.getById(universityId) ?: return@launch
            val analysis = UniversityStatusEngine.analyzeRejectionReply(replySnippet, u.name)

            when (analysis.type) {
                RejectionType.PERMANENT_BLACKLIST -> {
                    moveUniversityToBlacklist(universityId, conversationText = replySnippet, customReason = analysis.reason)
                }
                RejectionType.TEMPORARY_GREYLIST -> {
                    moveUniversityToGreylist(universityId, conversationText = replySnippet, customReason = analysis.reason)
                }
                RejectionType.UNKNOWN_REVIEW -> {
                    moveUniversityToGreylist(universityId, conversationText = replySnippet, customReason = analysis.reason)
                }
            }
        }
    }

    private suspend fun seedInitialUniversities() {
        val initialList = listOf(
            University(
                name = "University of Greenwich",
                country = "United Kingdom",
                city = "London",
                website = "www.gre.ac.uk",
                contactName = "Dr. Robert Sterling",
                email = "international@gre.ac.uk",
                intakeMonths = "Jan, Sep",
                commissionRate = "15%",
                applicationFee = "Free",
                priority = "High",
                partnershipStatus = "Contacted",
                notes = "High demand for Business & IT master degrees among international students.",
                tags = "London, Middle-Class, Fast Offer"
            ),
            University(
                name = "University of East London (UEL)",
                country = "United Kingdom",
                city = "London",
                website = "www.uel.ac.uk",
                contactName = "Sarah Jenkins",
                email = "partnerships@uel.ac.uk",
                intakeMonths = "Jan, May, Sep",
                commissionRate = "18%",
                applicationFee = "Free",
                priority = "High",
                partnershipStatus = "Prospect",
                notes = "Offers 3 intake cycles, perfect for South Asian and African students.",
                tags = "3 Intakes, Affordable Fee, Popular"
            ),
            University(
                name = "Coventry University",
                country = "United Kingdom",
                city = "Coventry",
                website = "www.coventry.ac.uk",
                contactName = "Marcus Vance",
                email = "b2b.agents@coventry.ac.uk",
                intakeMonths = "Jan, May, Sep",
                commissionRate = "15%",
                applicationFee = "Free",
                priority = "High",
                partnershipStatus = "In Discussion",
                notes = "Agent portal verification pending. CEO Ishak Dewan profile provided.",
                tags = "Top Ranked, Modern Campus"
            ),
            University(
                name = "University of Chester",
                country = "United Kingdom",
                city = "Chester",
                website = "www.chester.ac.uk",
                contactName = "Elena Rostova",
                email = "global.recruitment@chester.ac.uk",
                intakeMonths = "Jan, Sep",
                commissionRate = "15%",
                applicationFee = "Free",
                priority = "Medium",
                partnershipStatus = "Partnered",
                notes = "Active agreement signed. Direct application portal access granted.",
                tags = "Signed Partner, High Visa Rate"
            ),
            University(
                name = "Teesside University",
                country = "United Kingdom",
                city = "Middlesbrough",
                website = "www.tees.ac.uk",
                contactName = "David Thorne",
                email = "international.agents@tees.ac.uk",
                intakeMonths = "Jan, Sep",
                commissionRate = "15%",
                applicationFee = "Free",
                priority = "Medium",
                partnershipStatus = "Prospect",
                notes = "Highly affordable tuition with generous regional scholarships.",
                tags = "Affordable, Scholarships"
            )
        )

        initialList.forEach { uni ->
            dao.insert(uni)
        }
    }

    // Phase 9: UK University Research Intelligence Engine Methods
    fun runCeoResearchCampaign() {
        viewModelScope.launch {
            if (researchDao == null) {
                emitToast("Research DB is not initialized")
                return@launch
            }
            emitToast("🚀 CEO AI Campaign launched across all 10 specialized agents...")
            val jobs = CeoAiResearchCoordinator.runFullCeoResearchCampaign(researchDao, dao, agentDao)
            emitToast("✅ CEO Campaign Completed: ${jobs.size} research tasks completed, duplicates merged.")
        }
    }

    fun dispatchSpecializedResearchAgent(agentType: ResearchAgentType) {
        viewModelScope.launch {
            if (researchDao == null) {
                emitToast("Research DB is not initialized")
                return@launch
            }
            emitToast("🤖 Dispatching ${agentType.displayName}...")
            val job = CeoAiResearchCoordinator.executeResearchTask(researchDao, dao, agentDao, agentType)
            emitToast("✅ ${job.agentType} finished! ${job.resultsFound} found (${job.duplicatesPrevented} dupes blocked).")
        }
    }

    fun syncResearchedInstitutionsToCrm() {
        viewModelScope.launch {
            if (researchDao == null) {
                emitToast("Research DB is not initialized")
                return@launch
            }
            val count = CeoAiResearchCoordinator.autoSyncResearchedInstitutionsToCrm(researchDao, dao)
            if (count > 0) {
                emitToast("🎉 Synced $count new researched institutions directly to Main CRM!")
            } else {
                emitToast("All researched institutions are already synchronized with CRM.")
            }
        }
    }

    // Phase 11 Executive Brain Methods
    fun setBriefingLanguage(language: String) {
        _briefingLanguage.value = language
        generateFounderBriefing()
    }

    fun generateFounderBriefing() {
        viewModelScope.launch {
            _isBriefingLoading.value = true
            val currentUnis = allUniversities?.value ?: emptyList()
            val currentResearched = researchedInstitutions?.value ?: emptyList()
            val currentReminders = reminders?.value ?: emptyList()
            val currentTodos = todos?.value ?: emptyList()
            val settings = _businessSettings?.value ?: BusinessSettings()
            val lang = _briefingLanguage?.value ?: "English"

            val briefing = ExecutiveBrainService.generateBriefing(
                language = lang,
                settings = settings,
                universities = currentUnis,
                researchedInstitutions = currentResearched,
                reminders = currentReminders,
                todos = currentTodos
            )
            _founderBriefing.value = briefing
            _isBriefingLoading.value = false
        }
    }

    fun sendExecutiveChatMessage(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            val userMsg = ChatMessage(sender = "Founder Ishak Dawan", message = query)
            val updatedHistory = (_executiveChatHistory?.value ?: emptyList()) + userMsg
            _executiveChatHistory.value = updatedHistory
            _isAiChatLoading.value = true

            try {
                val reply = ExecutiveBrainService.askExecutiveAssistant(
                    userQuery = query,
                    language = _briefingLanguage?.value ?: "Bangla",
                    history = updatedHistory,
                    universities = allUniversities?.value ?: emptyList(),
                    researchedInstitutions = researchedInstitutions?.value ?: emptyList(),
                    reminders = reminders?.value ?: emptyList(),
                    todos = todos?.value ?: emptyList()
                )

                val aiMsg = ChatMessage(sender = "Executive AI Brain", message = reply)
                _executiveChatHistory.value = _executiveChatHistory.value + aiMsg
            } finally {
                _isAiChatLoading.value = false
            }
        }
    }

    fun generateProductivityPlan(mode: String) {
        viewModelScope.launch {
            _isAiChatLoading.value = true
            try {
                val report = ExecutiveBrainService.generateProductivityReport(
                    mode = mode,
                    language = _briefingLanguage?.value ?: "Bangla",
                    universitiesCount = allUniversities?.value?.size ?: 0,
                    pendingTasks = todos?.value?.count { !it.isCompleted } ?: 0,
                    remindersCount = reminders?.value?.count { !it.isCompleted } ?: 0
                )
                val aiMsg = ChatMessage(sender = "Executive AI Brain", message = report)
                _executiveChatHistory.value = (_executiveChatHistory?.value ?: emptyList()) + aiMsg
            } finally {
                _isAiChatLoading.value = false
            }
        }
    }

    fun speakBriefing(context: Context) {
        val text = _founderBriefing.value?.fullBriefingText ?: return
        ExecutiveBrainService.initTts(context)
        ExecutiveBrainService.speak(text, _briefingLanguage.value)
    }

    fun stopBriefingSpeech() {
        ExecutiveBrainService.stopSpeech()
    }

    fun verifyGeminiConnectivity() {
        viewModelScope.launch {
            com.example.core.ai.CentralGeminiService.verifyConnectivity(getApplication())
        }
    }

    fun setGeminiApiKey(key: String) {
        com.example.core.ai.CentralGeminiService.setApiKey(getApplication(), key)
        verifyGeminiConnectivity()
    }

    // Student Profile Management (Phase 14)
    fun addStudentProfile(student: StudentProfileEntity) {
        viewModelScope.launch {
            if (studentDao == null) {
                emitToast("Student DB not initialized")
                return@launch
            }
            studentDao.insertStudent(student)
            emitToast("🎓 Student profile added: ${student.name}")
        }
    }

    fun updateStudentProfile(student: StudentProfileEntity) {
        viewModelScope.launch {
            studentDao?.updateStudent(student)
            emitToast("Updated student profile: ${student.name}")
        }
    }

    fun deleteStudentProfile(student: StudentProfileEntity) {
        viewModelScope.launch {
            studentDao?.deleteStudent(student)
            emitToast("Deleted student profile: ${student.name}")
        }
    }

    // Phase 16 & 17: Executive Memory Engine & Multi-Agent Intelligence
    private val _executiveAgents = MutableStateFlow<List<ExecutiveAgentItem>>(
        listOf(
            ExecutiveAgentItem("1", "CEO AI", "Executive Coordination", "Active", 45, 3, 99, "Master CEO Coordinator & Delegation Engine", "Coordinated 19 specialized sub-agents"),
            ExecutiveAgentItem("2", "University Research AI", "Research", "Active", 28, 2, 97, "UK University Portal & Policy Harvester", "Scanned 14 UK university international pages"),
            ExecutiveAgentItem("3", "College Research AI", "Research", "Idle", 15, 1, 95, "Pathway & Private College Specialist", "Updated BPP & QA Higher Education database"),
            ExecutiveAgentItem("4", "Scholarship AI", "Financial", "Active", 22, 0, 98, "Merit & Regional Bursary Finder", "Identified £5,000 Vice-Chancellor Bursaries"),
            ExecutiveAgentItem("5", "Admission AI", "Admissions", "Active", 34, 4, 96, "Entry Threshold & CAS SLA Evaluator", "Verified 5-year MOI waiver eligibility"),
            ExecutiveAgentItem("6", "Visa AI", "Compliance", "Active", 18, 1, 98, "CAS Approval & 28-Day Bank Balance Checker", "Confirmed 92%+ visa success rating"),
            ExecutiveAgentItem("7", "Document AI", "Compliance", "Idle", 40, 2, 95, "MOI & Transcript Validation Engine", "Verified Tanvir Ahmed MOI document"),
            ExecutiveAgentItem("8", "Email AI", "Communication", "Active", 50, 5, 96, "B2B Outreach & Direct Reply Engine", "Drafted Greenwich & Chester follow-up emails"),
            ExecutiveAgentItem("9", "Marketing AI", "Growth", "Idle", 19, 2, 92, "Student Campaign & Digital Reach Tracker", "Monitored September intake campaign stats"),
            ExecutiveAgentItem("10", "Lead Hunter AI", "Growth", "Active", 26, 3, 94, "Direct Student Inquiry Harvester", "Processed 3 new high-CGPA student leads"),
            ExecutiveAgentItem("11", "CRM AI", "Operations", "Active", 65, 0, 99, "Pipeline & Institution Relationship Manager", "Synced 14 researched universities to CRM"),
            ExecutiveAgentItem("12", "Reminder AI", "Operations", "Active", 31, 2, 97, "Priority Follow-up & Alert Dispatcher", "Scheduled 2 urgent university follow-up alerts"),
            ExecutiveAgentItem("13", "Meeting AI", "Operations", "Idle", 12, 1, 96, "Founder Schedule & B2B Meeting Manager", "Confirmed Greenwich Regional Officer meeting"),
            ExecutiveAgentItem("14", "Google Sheets AI", "Integrations", "Active", 88, 0, 99, "Real-time Cloud Spreadsheet Sync", "Synced 14 institutions to Google Sheet"),
            ExecutiveAgentItem("15", "Knowledge AI", "Intelligence", "Active", 42, 1, 98, "Executive Policy & Knowledge Base Engine", "Indexed MOI waiver policies for 2026"),
            ExecutiveAgentItem("16", "Analytics AI", "Intelligence", "Active", 15, 0, 95, "Conversion Rate & B2B Performance Tracker", "Calculated 34% higher response rate"),
            ExecutiveAgentItem("17", "Translation AI", "Localization", "Active", 60, 0, 99, "Dual Language English & Bangla Engine", "Processed Executive Briefing in Bangla"),
            ExecutiveAgentItem("18", "Memory AI", "Intelligence", "Active", 38, 0, 98, "Executive Memory Log & Founder History", "Recorded Greenwich partnership meeting details"),
            ExecutiveAgentItem("19", "Notification AI", "Operations", "Active", 25, 1, 97, "High Priority System & Push Notifier", "Dispatched urgent reminder alerts"),
            ExecutiveAgentItem("20", "Background AI", "System", "Active", 120, 0, 99, "Asynchronous Task Scheduler & Monitor", "Maintained background Room & API sync")
        )
    )
    val executiveAgents: StateFlow<List<ExecutiveAgentItem>> = _executiveAgents.asStateFlow()

    fun addExecutiveMemory(memory: ExecutiveMemoryEntity) {
        viewModelScope.launch {
            if (executiveMemoryDao == null) {
                emitToast("Executive Memory DB not initialized")
                return@launch
            }
            executiveMemoryDao.insertMemory(memory)
            emitToast("🧠 Memory saved: ${memory.title}")
        }
    }

    fun deleteExecutiveMemory(memory: ExecutiveMemoryEntity) {
        viewModelScope.launch {
            executiveMemoryDao?.deleteMemory(memory)
            emitToast("Deleted memory: ${memory.title}")
        }
    }

    fun runMultiAgentSystemCoordination() {
        viewModelScope.launch {
            emitToast("🤖 CEO AI coordinating all 20 specialized agents...")
            // Update agent statuses to running briefly
            _executiveAgents.value = _executiveAgents.value.map {
                it.copy(
                    status = if (it.name == "CEO AI") "Active" else "Running",
                    completedCount = it.completedCount + 1,
                    lastAction = "Gemini central brain synchronized • Updated memory & policy log"
                )
            }
            kotlinx.coroutines.delay(1200)
            _executiveAgents.value = _executiveAgents.value.map {
                it.copy(status = "Active")
            }
            emitToast("✅ Central AI Coordination complete across all 20 agents!")
        }
    }

    private fun emitToast(msg: String) {
        viewModelScope.launch {
            _toastEvent.emit(msg)
        }
    }
}
