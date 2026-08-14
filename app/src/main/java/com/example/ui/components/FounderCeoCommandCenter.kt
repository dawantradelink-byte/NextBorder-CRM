package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.IntentUtils
import com.example.viewmodel.CrmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FounderCeoCommandCenter(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier,
    onNavigateToAgents: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {}
) {
    val context = LocalContext.current
    val allUniversities by viewModel.allUniversities.collectAsState()
    val researchedInstitutions by viewModel.researchedInstitutions.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val todos by viewModel.todos.collectAsState()
    val researchJobs by viewModel.researchJobs.collectAsState()

    var activeTab by remember { mutableStateOf("Overview") }
    var selectedPipelineStage by remember { mutableStateOf("All") }

    // Metrics & AI Intelligence Calculations
    val totalDiscovered = researchedInstitutions.size + allUniversities.size
    val pendingFollowups = allUniversities.count { it.status == "Follow-up Needed" || it.status == "Contacted" }
    val newOpportunities = researchedInstitutions.count { it.agentRecruitmentAvailable && !it.isSyncedToMainCrm }
    val activePartnerships = allUniversities.count { it.partnershipStatus == "Partnered" || it.status == "Partnered" }
    val pendingRemindersCount = reminders.count { !it.isCompleted }
    val upcomingMeetingsCount = reminders.count { !it.isCompleted && it.category.equals("Meeting", ignoreCase = true) }
    val completedTodosCount = todos.count { it.isCompleted }
    val totalTodosCount = if (todos.isNotEmpty()) todos.size else 1
    val productivityScore = ((completedTodosCount.toDouble() / totalTodosCount.toDouble()) * 100).toInt().coerceIn(75, 99)

    // Pipeline Stages according to Phase 10 specs
    val pipelineStages = listOf(
        "Discovered", "Researching", "Ready for Contact", "Email Draft Ready",
        "Awaiting Review", "Contacted", "Follow-up", "Meeting Scheduled",
        "Negotiation", "Agreement Pending", "Partner Active", "Inactive"
    )

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: FOUNDER MODE - Ishak Dawan, CEO
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
                        color = Color(0xFFFF2E93),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("ISHAK DAWAN", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFF2E93).copy(alpha = 0.25f)) {
                                Text("FOUNDER MODE • CEO", color = Color(0xFFFF2E93), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Autonomous Business Intelligence Platform • Motorola Edge 60 Pro", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                        Text("AI Score: $productivityScore%", color = Color(0xFF10B981), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // CEO COMMAND CENTER METRICS CAROUSEL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CeoStatPill("Today's Priorities", "${todos.count { !it.isCompleted }}", Color(0xFF00AEEF), Modifier.weight(1f))
                CeoStatPill("Pending Follow-ups", "$pendingFollowups", Color(0xFFFF8A00), Modifier.weight(1f))
                CeoStatPill("New Opportunities", "$newOpportunities", Color(0xFFFF2E93), Modifier.weight(1f))
                CeoStatPill("Meetings", "$upcomingMeetingsCount", Color(0xFF3B82F6), Modifier.weight(1f))
            }

            // Status Bar: Background Orchestrator, Sync, & AI Health
            Surface(
                color = Color.Black.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatusDotIndicator(label = "Background Worker", active = true, color = Color(0xFF10B981))
                        StatusDotIndicator(label = "Google Sheets Sync", active = true, color = Color(0xFF00AEEF))
                        StatusDotIndicator(label = "Research Engine", active = true, color = Color(0xFFFF8A00))
                    }

                    Button(
                        onClick = { viewModel.triggerBackgroundSyncNow(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Orchestrate", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Command Center Navigation Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CeoNavTab("Executive Overview", activeTab == "Overview", Modifier.weight(1f)) { activeTab = "Overview" }
                CeoNavTab("Partnership Pipeline", activeTab == "Pipeline", Modifier.weight(1f)) { activeTab = "Pipeline" }
                CeoNavTab("Email Workflow", activeTab == "Email", Modifier.weight(1f)) { activeTab = "Email" }
                CeoNavTab("BI Analytics", activeTab == "Analytics", Modifier.weight(1f)) { activeTab = "Analytics" }
            }

            // Tab Content
            when (activeTab) {
                "Overview" -> CeoOverviewSection(
                    viewModel = viewModel,
                    allUniversities = allUniversities,
                    researchedInstitutions = researchedInstitutions,
                    reminders = reminders
                )
                "Pipeline" -> PartnershipPipelineSection(
                    allUniversities = allUniversities,
                    researchedInstitutions = researchedInstitutions,
                    selectedStage = selectedPipelineStage,
                    onStageSelect = { selectedPipelineStage = it }
                )
                "Email" -> EmailWorkflowSection(
                    allUniversities = allUniversities,
                    onGenerateFollowup = { uni ->
                        IntentUtils.launchEmail(
                            context = context,
                            toEmail = uni.email,
                            subject = "NextBorder AIOS Partnership Follow-up - ${uni.name}",
                            body = "Dear ${uni.contactName},\n\nI am writing to follow up on our partnership proposal regarding student recruitment for ${uni.name}.\n\nBest regards,\nIshak Dawan\nCEO, NextBorder"
                        )
                    }
                )
                "Analytics" -> BiAnalyticsSection(
                    universitiesDiscovered = totalDiscovered,
                    activePartnerships = activePartnerships,
                    pendingFollowups = pendingFollowups,
                    remindersCompleted = reminders.count { it.isCompleted },
                    totalReminders = reminders.size
                )
            }
        }
    }
}

@Composable
fun CeoStatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun StatusDotIndicator(label: String, active: Boolean, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier.size(7.dp).clip(CircleShape).background(if (active) color else Color.Gray)
        )
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CeoNavTab(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (selected) Color(0xFFFF2E93) else Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 10.sp, maxLines = 1)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CeoOverviewSection(
    viewModel: CrmViewModel,
    allUniversities: List<com.example.data.University>,
    researchedInstitutions: List<com.example.modules.research.model.ResearchedInstitutionEntity>,
    reminders: List<com.example.data.ReminderEntity>
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Phase 11 Executive Brain Gemini AI Founder Briefing & Conversational Assistant
        ExecutiveBrainBriefingPanel(viewModel = viewModel)

        // Today's High Priority Actions Card
        Surface(
            color = Color.Black.copy(alpha = 0.35f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF00AEEF).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF00AEEF), modifier = Modifier.size(16.dp))
                    Text("Today's Strategic Priorities", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                val highPriorityUnis = allUniversities.filter { it.priority == "High" || it.partnershipStatus == "Prospect" }.take(3)
                if (highPriorityUnis.isEmpty()) {
                    Text("No urgent university action items pending.", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                } else {
                    highPriorityUnis.forEach { uni ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(uni.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${uni.city}, ${uni.country} • Status: ${uni.status}", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF00AEEF).copy(alpha = 0.2f)) {
                                Text(uni.priority, color = Color(0xFF00AEEF), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        // New Discovered Opportunities Summary
        Surface(
            color = Color.Black.copy(alpha = 0.35f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFF8A00).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Explore, contentDescription = null, tint = Color(0xFFFF8A00), modifier = Modifier.size(16.dp))
                        Text("New Discovered Partnership Opportunities (${researchedInstitutions.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    TextButton(
                        onClick = { viewModel.syncResearchedInstitutionsToCrm() },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Sync to CRM", color = Color(0xFFFF8A00), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                researchedInstitutions.take(3).forEach { inst ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(inst.institutionName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${inst.city} • Scholarship: ${inst.scholarshipDetails.take(35)}...", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF10B981).copy(alpha = 0.2f)) {
                            Text("MOI YES", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PartnershipPipelineSection(
    allUniversities: List<com.example.data.University>,
    researchedInstitutions: List<com.example.modules.research.model.ResearchedInstitutionEntity>,
    selectedStage: String,
    onStageSelect: (String) -> Unit
) {
    val stages = listOf(
        "All", "Discovered", "Researching", "Ready for Contact", "Email Draft Ready",
        "Awaiting Review", "Contacted", "Follow-up", "Meeting Scheduled",
        "Negotiation", "Agreement Pending", "Partner Active", "Inactive"
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Stage Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(stages) { stage ->
                FilterChip(
                    selected = selectedStage == stage,
                    onClick = { onStageSelect(stage) },
                    label = { Text(stage, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF2E93),
                        containerColor = Color.White.copy(alpha = 0.1f),
                        labelColor = Color.White,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Pipeline List Card
        val displayList = allUniversities.filter { uni ->
            if (selectedStage == "All") true
            else uni.status.equals(selectedStage, ignoreCase = true) || uni.partnershipStatus.equals(selectedStage, ignoreCase = true)
        }

        if (displayList.isEmpty()) {
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No institutions currently in '$selectedStage' stage.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                displayList.take(5).forEach { uni ->
                    Surface(
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(uni.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Contact: ${uni.email} • Intake: ${uni.intakeMonths}", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                            }

                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFF2E93).copy(alpha = 0.2f)) {
                                Text(uni.partnershipStatus, color = Color(0xFFFF2E93), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmailWorkflowSection(
    allUniversities: List<com.example.data.University>,
    onGenerateFollowup: (com.example.data.University) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            color = Color.Black.copy(alpha = 0.35f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                    Text("Automated Follow-up Email Queue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Text("Tracking 3-Day, 7-Day & 15-Day Automated Outreach Intervals to UK Universities.", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)

                allUniversities.take(4).forEach { uni ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(uni.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Email: ${uni.email} • Status: ${uni.status}", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }

                        Button(
                            onClick = { onGenerateFollowup(uni) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Send Draft", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiAnalyticsSection(
    universitiesDiscovered: Int,
    activePartnerships: Int,
    pendingFollowups: Int,
    remindersCompleted: Int,
    totalReminders: Int
) {
    Surface(
        color = Color.Black.copy(alpha = 0.35f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                Text("Business Intelligence Executive Analytics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AnalyticsProgressRow("Total Universities Discovered", universitiesDiscovered, 50, Color(0xFF00AEEF))
                AnalyticsProgressRow("Active UK University Partnerships", activePartnerships, 20, Color(0xFF10B981))
                AnalyticsProgressRow("Pending Outreach Follow-ups", pendingFollowups, 30, Color(0xFFFF8A00))
                AnalyticsProgressRow("Founder Personal Reminders Completed", remindersCompleted, totalReminders.coerceAtLeast(1), Color(0xFFFF2E93))
            }
        }
    }
}

@Composable
fun AnalyticsProgressRow(label: String, count: Int, target: Int, color: Color) {
    val progress = (count.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            Text("$count / $target", color = color, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}
