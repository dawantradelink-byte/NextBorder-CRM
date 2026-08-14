package com.example.modules.research.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudentProfileEntity
import com.example.modules.research.engine.StudentMatchResult
import com.example.modules.research.engine.StudentMatchingEngine
import com.example.ui.components.GlassCard
import com.example.viewmodel.CrmViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentMatchingPanel(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val studentProfiles by viewModel.studentProfiles.collectAsState()
    val researchedInstitutions by viewModel.researchedInstitutions.collectAsState()
    val universities by viewModel.allUniversities.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val todos by viewModel.todos.collectAsState()

    var selectedStudentIndex by remember { mutableIntStateOf(0) }
    var selectedSmartFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddStudentDialog by remember { mutableStateOf(false) }

    // Selected Student Profile
    val currentStudent = studentProfiles.getOrNull(selectedStudentIndex)

    // Calculate Matches
    val rawMatches = if (currentStudent != null) {
        StudentMatchingEngine.matchStudentWithInstitutions(currentStudent, researchedInstitutions)
    } else {
        emptyList()
    }

    // Apply Smart Search & Filters
    val filteredMatches = rawMatches.filter { match ->
        val inst = match.institution
        val matchesQuery = searchQuery.isBlank() ||
            inst.institutionName.contains(searchQuery, ignoreCase = true) ||
            match.suggestedCourse.contains(searchQuery, ignoreCase = true) ||
            inst.city.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedSmartFilter) {
            "MOI Accepted" -> match.moiAccepted
            "Interview Accepted" -> match.interviewAccepted
            "Low IELTS Req" -> inst.minIeltsScore <= 6.0
            "Scholarship" -> match.scholarshipAvailable
            "Low Tuition" -> match.estimatedTuitionFee.contains("13") || match.estimatedTuitionFee.contains("14")
            "High Visa Success" -> inst.recruitmentSuitability.contains("High", ignoreCase = true)
            "Agent Partnership" -> inst.agentRecruitmentAvailable
            else -> true
        }

        matchesQuery && matchesFilter
    }

    // Founder Productivity Center Metrics
    val pendingFollowups = universities.count { it.status == "Follow-up Needed" || it.partnershipStatus == "Prospect" }
    val importantMeetings = reminders.count { !it.isCompleted && it.category.equals("Meeting", ignoreCase = true) }
    val pendingDocuments = universities.count { it.notes.contains("Document", ignoreCase = true) || it.notes.contains("CAS", ignoreCase = true) }
    val studentsWaiting = studentProfiles.size
    val universitiesWaitingReply = universities.count { it.status == "Contacted" || it.status == "Email Draft Ready" }
    val expiredTasks = todos.count { !it.isCompleted && (it.createdAt < System.currentTimeMillis() - 86400000L * 3) }
    val upcomingDeadlines = reminders.count { !it.isCompleted && (it.triggerTimeMs < System.currentTimeMillis() + 86400000L * 7) }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF10B981),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("STUDENT MATCHING ENGINE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF10B981).copy(alpha = 0.25f)) {
                                Text("PHASE 14", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Opportunity Intelligence & Admission Fit Matrix", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = { showAddStudentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // Founder Productivity Center Overview Tracker
            Surface(
                color = Color.Black.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF00AEEF).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color(0xFF00AEEF), modifier = Modifier.size(16.dp))
                        Text("FOUNDER PRODUCTIVITY CENTER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProductivityChip("Follow-ups Pending", "$pendingFollowups", Color(0xFFFF8A00))
                        ProductivityChip("Meetings", "$importantMeetings", Color(0xFFEC4899))
                        ProductivityChip("Pending Docs", "$pendingDocuments", Color(0xFFA78BFA))
                        ProductivityChip("Students Waiting", "$studentsWaiting", Color(0xFF10B981))
                        ProductivityChip("Uni Replies Waiting", "$universitiesWaitingReply", Color(0xFF00AEEF))
                        ProductivityChip("Expired Tasks", "$expiredTasks", if (expiredTasks > 0) Color(0xFFEF4444) else Color(0xFF10B981))
                        ProductivityChip("Upcoming Deadlines", "$upcomingDeadlines", Color(0xFFFF2E93))
                    }
                }
            }

            // Student Profile Selector Carousel
            if (studentProfiles.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("SELECT STUDENT PROFILE FOR MATCHING", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(studentProfiles.size) { idx ->
                            val s = studentProfiles[idx]
                            val isSelected = idx == selectedStudentIndex
                            Surface(
                                color = if (isSelected) Color(0xFF10B981) else Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.clickable { selectedStudentIndex = idx }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Column {
                                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${s.preferredSubject} • CGPA ${s.cgpa}", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Active Student Details Summary Card
            currentStudent?.let { student ->
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👤 Student Profile: ${student.name} (${student.country})", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            Text("Budget: £${student.budget.toInt()}", color = Color(0xFFFF8A00), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Text("🎓 Academic: ${student.bachelorDegree} • CGPA ${student.cgpa} • Gap: ${student.gapYears} Year", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                        Text("🗣️ English Qualification: ${student.englishQualification} (IELTS: ${student.ieltsOverall}, MOI: ${if (student.moiAvailable) "YES" else "NO"})", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                        Text("🎯 Preferred: ${student.preferredSubject} • ${student.preferredCity} • ${student.preferredIntake}", color = Color(0xFF00AEEF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Smart Search & Filters Bar (Phase 14)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Smart search course, institution, city...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                val smartFilterOptions = listOf(
                    "All",
                    "MOI Accepted",
                    "Interview Accepted",
                    "Low IELTS Req",
                    "Scholarship",
                    "Low Tuition",
                    "High Visa Success",
                    "Agent Partnership"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(smartFilterOptions) { opt ->
                        FilterChip(
                            selected = selectedSmartFilter == opt,
                            onClick = { selectedSmartFilter = opt },
                            label = { Text(opt, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF10B981),
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Suggested Matches List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("MATCHED INSTITUTION OPPORTUNITIES (${filteredMatches.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                if (filteredMatches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No matching institution found for the selected smart filter.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                } else {
                    filteredMatches.take(10).forEach { match ->
                        MatchResultCard(match)
                    }
                }
            }
        }
    }

    // Add Student Profile Dialog
    if (showAddStudentDialog) {
        AddStudentDialog(
            onDismiss = { showAddStudentDialog = false },
            onAddStudent = { newStudent ->
                viewModel.addStudentProfile(newStudent)
                showAddStudentDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MatchResultCard(match: StudentMatchResult) {
    val inst = match.institution

    Surface(
        color = Color.Black.copy(alpha = 0.45f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Title & Match Score Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(inst.institutionName, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text(match.suggestedCourse, color = Color(0xFF00AEEF), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        match.matchScore >= 80 -> Color(0xFF10B981)
                        match.matchScore >= 60 -> Color(0xFFFF8A00)
                        else -> Color(0xFFEF4444)
                    }
                ) {
                    Text("Match: ${match.matchScore}/100", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            // Requirements & Features Badges
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MatchChip("MOI Accepted: ${if (match.moiAccepted) "YES 🎓" else "NO"}", if (match.moiAccepted) Color(0xFF10B981) else Color(0xFFEF4444))
                MatchChip("Interview: ${if (match.interviewAccepted) "YES 🗣️" else "NO"}", Color(0xFF7C3AED))
                MatchChip("Min IELTS: ${match.minEnglishReq}", Color(0xFF00AEEF))
                MatchChip("Est Tuition: ${match.estimatedTuitionFee}", Color(0xFFFF8A00))
                MatchChip("App Fee: ${match.applicationFee}", Color(0xFFEC4899))
                MatchChip("Deadline: ${match.applicationDeadline}", Color(0xFFA78BFA))
            }

            if (match.scholarshipAvailable) {
                Text("🎓 Scholarship: ${match.scholarshipDetails}", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            // CEO Insights Box
            Surface(
                color = Color(0xFFFF8A00).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFF8A00).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFF8A00), modifier = Modifier.size(14.dp))
                    Text("CEO Insights: ${match.ceoInsights}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ProductivityChip(label: String, count: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Text(count, color = color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun MatchChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onAddStudent: (StudentProfileEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var background by remember { mutableStateOf("BSc in Computer Science") }
    var cgpaText by remember { mutableStateOf("3.40") }
    var preferredSubject by remember { mutableStateOf("Computer Science") }
    var budgetText by remember { mutableStateOf("16000") }
    var englishQual by remember { mutableStateOf("MOI & IELTS 6.5") }
    var ieltsText by remember { mutableStateOf("6.5") }
    var moiAvailable by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Student Profile", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = background,
                    onValueChange = { background = it },
                    label = { Text("Academic Background") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cgpaText,
                    onValueChange = { cgpaText = it },
                    label = { Text("CGPA") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = preferredSubject,
                    onValueChange = { preferredSubject = it },
                    label = { Text("Preferred Subject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    label = { Text("Budget (£ GBP)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = englishQual,
                    onValueChange = { englishQual = it },
                    label = { Text("English Qualification") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val cgpaVal = cgpaText.toDoubleOrNull() ?: 3.0
                        val budgetVal = budgetText.toDoubleOrNull() ?: 15000.0
                        val ieltsVal = ieltsText.toDoubleOrNull() ?: 6.0
                        val newStudent = StudentProfileEntity(
                            name = name,
                            academicBackground = background,
                            bachelorDegree = "$background (CGPA $cgpaVal)",
                            cgpa = cgpaVal,
                            preferredSubject = preferredSubject,
                            budget = budgetVal,
                            englishQualification = englishQual,
                            ieltsOverall = ieltsVal,
                            moiAvailable = moiAvailable
                        )
                        onAddStudent(newStudent)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Add Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = Color(0xFF1E1035)
    )
}
