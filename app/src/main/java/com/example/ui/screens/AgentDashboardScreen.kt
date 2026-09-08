package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.AgentLogEntity
import com.example.data.AgentMemoryEntity
import com.example.data.University
import com.example.data.UniversityReplyEntity
import com.example.ui.components.AnimatedAgentAvatar
import com.example.ui.components.GlassCard
import com.example.modules.research.ui.ResearchIntelligencePanel
import com.example.util.GoogleSheetsSyncService
import com.example.util.NonReplyDiagnosis
import com.example.viewmodel.CrmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDashboardScreen(
    viewModel: CrmViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedSection by remember { mutableStateOf("Research Intelligence") }

    var selectedDiagnosisUni by remember { mutableStateOf<University?>(null) }
    var currentDiagnosis by remember { mutableStateOf<NonReplyDiagnosis?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Autonomous AI Agent Command Center", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("4 Self-Evolving Agents • Shared Long-Term Memory", color = Color(0xFFFF8A00), fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Quick Google Sheets Sync Button in TopBar
                    IconButton(onClick = { viewModel.syncLeadsToGoogleSheets(context) }) {
                        Icon(Icons.Default.TableChart, contentDescription = "Sync to Google Sheets", tint = Color(0xFF10B981))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Top Navigation Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { AgentTab("Research Intelligence", selectedSection == "Research Intelligence") { selectedSection = "Research Intelligence" } }
                item { AgentTab("Command Center", selectedSection == "Command Center") { selectedSection = "Command Center" } }
                item { AgentTab("University Replies", selectedSection == "Replies") { selectedSection = "Replies" } }
                item { AgentTab("Shared Memory Bank", selectedSection == "Memory") { selectedSection = "Memory" } }
                item { AgentTab("Live Execution Logs", selectedSection == "Logs") { selectedSection = "Logs" } }
                item { AgentTab("Outreach Templates", selectedSection == "Templates") { selectedSection = "Templates" } }
                item { AgentTab("Plugins", selectedSection == "Plugins") { selectedSection = "Plugins" } }
            }

            when (selectedSection) {
                "Research Intelligence" -> ResearchIntelligencePanel(viewModel)
                "Command Center" -> AgentCommandCenterSection(viewModel, onDiagnoseClick = { uni ->
                    viewModel.diagnoseNonReply(uni) { diag ->
                        selectedDiagnosisUni = uni
                        currentDiagnosis = diag
                    }
                })
                "Replies" -> UniversityRepliesSection(viewModel)
                "Memory" -> SharedMemoryBankSection(viewModel)
                "Logs" -> AgentLiveLogsSection(viewModel)
                "Templates" -> EmailTemplateEditor(viewModel)
                "Plugins" -> PluginsDashboardSection()
            }
        }
    }

    // Non-Reply Diagnosis Modal Dialog
    currentDiagnosis?.let { diag ->
        AlertDialog(
            onDismissRequest = { currentDiagnosis = null },
            containerColor = Color(0xFF1F2937),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFFFF8A00))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Non-Reply Strategy Adaptation", color = Color.White, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Target: ${diag.universityName}", color = Color.White, fontWeight = FontWeight.Bold)
                    
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)).padding(10.dp)) {
                        Column {
                            Text("PROBABLE NON-REPLY REASON", color = Color(0xFFFF2E93), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(diag.probableReason, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)).padding(10.dp)) {
                        Column {
                            Text("RECOMMENDED RE-ENGAGEMENT STRATEGY", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(diag.recommendedStrategy, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Text("Suggested Subject:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text(diag.suggestedSubjectLine, color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uni = selectedDiagnosisUni
                        if (uni != null) {
                            viewModel.addContactLog(uni.id, "AI Strategy Re-engagement", "Strategy adapted: ${diag.recommendedStrategy}")
                            viewModel.addNote(uni.id, "AI Non-Reply Diagnosis: ${diag.probableReason}. Action: ${diag.recommendedStrategy}")
                        }
                        currentDiagnosis = null
                        Toast.makeText(context, "Re-engagement strategy saved to CRM note!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00))
                ) {
                    Text("Apply Strategy & Log Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { currentDiagnosis = null }) {
                    Text("Close", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}

@Composable
fun AgentTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFFF2E93) else Color.White.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AgentCommandCenterSection(viewModel: CrmViewModel, onDiagnoseClick: (University) -> Unit) {
    val context = LocalContext.current
    val isRunning by viewModel.isAgentRunning.collectAsState()
    val activeUnis by viewModel.activeUniversities.collectAsState()
    val unrepliedUnis = remember(activeUnis) { activeUnis.filter { it.partnershipStatus == "Contacted" || it.partnershipStatus == "Prospect" } }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        // Quick Action Hub & Google Sheets Drive Banner
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AUTONOMOUS 4-AGENT SWARM", color = Color(0xFFFF8A00), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Self-Evolving Long-Term Memory • Daily Knowledge Sync", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.runAutonomousAgentCycle() },
                            enabled = !isRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E93))
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Running...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run Swarm Cycle", fontSize = 11.sp)
                            }
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.1f))

                    // Google Sheets Drive Integration Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Google Sheets Drive Sync (${GoogleSheetsSyncService.GOOGLE_DRIVE_ACCOUNT})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Sync lead generation table directly to your Google Drive folder.", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        }
                        Button(
                            onClick = { viewModel.syncLeadsToGoogleSheets(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Sync Table", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 4 Animated AI Agent Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ACTIVE AGENT SWARM (4 SPECIALIZED UNITS)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                AgentCardItem(
                    agentType = "Partnerships",
                    name = "Gabriel (UK University Specialist)",
                    role = "UK Lead Discovery & Human Email Proposals",
                    status = if (isRunning) "Scanning UK Admissions..." else "Active & Self-Evolving",
                    memoryExcerpt = "Optimized UK B2B proposal angle focusing on USA graduate CEO profile."
                )

                AgentCardItem(
                    agentType = "Strategy",
                    name = "Strategy & Reply Analyzer",
                    role = "University Inbox Tracker & Strategy Adaptation",
                    status = if (isRunning) "Diagnosing Delays..." else "Active & Diagnostic",
                    memoryExcerpt = "Re-engages non-reply universities with direct intake quotes after 7 days."
                )

                AgentCardItem(
                    agentType = "HR",
                    name = "HR & Recruitment Officer",
                    role = "Counselor Onboarding & Team KPI Guidelines",
                    status = "Active & Monitoring",
                    memoryExcerpt = "Enforces pre-screening file checklist before student file submission."
                )

                AgentCardItem(
                    agentType = "Security",
                    name = "Security & Compliance Officer",
                    role = "Biometric Security & Email Deliverability Audit",
                    status = "Active & Guarding",
                    memoryExcerpt = "SPF/DKIM clean. Human approval rule active for all outbound outreach."
                )
            }
        }

        // University Non-Reply Diagnostic Tool Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FindInPage, contentDescription = null, tint = Color(0xFFFF8A00))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DIAGNOSE WHY UNIVERSITIES HAVEN'T REPLIED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text("Select a university to analyze response delay triggers and generate a new re-engagement angle.", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)

                    if (unrepliedUnis.isEmpty()) {
                        Text("No pending contacted universities found.", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            unrepliedUnis.take(4).forEach { uni ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .clickable { onDiagnoseClick(uni) }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(uni.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Status: ${uni.partnershipStatus} • Country: ${uni.country}", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { onDiagnoseClick(uni) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Diagnose", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgentCardItem(
    agentType: String,
    name: String,
    role: String,
    status: String,
    memoryExcerpt: String
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedAgentAvatar(agentType = agentType, size = 52.dp, isActive = true)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(role, color = Color(0xFFFF8A00), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Memory: $memoryExcerpt", color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (status.contains("Active")) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFFF2E93).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(status, color = if (status.contains("Active")) Color(0xFF10B981) else Color(0xFFFF2E93), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun UniversityRepliesSection(viewModel: CrmViewModel) {
    val replies by viewModel.universityReplies.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MarkEmailUnread, contentDescription = null, tint = Color(0xFFFF8A00))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("INSTANT UNIVERSITY INBOX & NOTIFICATION FEED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Strategy Agent automatically parses replies for sentiment & next actions.", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }
        }

        if (replies.isEmpty()) {
            item {
                Text("No university replies received yet.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, modifier = Modifier.padding(16.dp))
            }
        } else {
            items(replies) { reply ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(reply.universityName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))

                            val sentimentColor = when (reply.sentiment) {
                                "Positive" -> Color(0xFF10B981)
                                "Needs Followup" -> Color(0xFFFF8A00)
                                else -> Color(0xFF60A5FA)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(sentimentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(reply.sentiment, color = sentimentColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }

                        Text(reply.subject, color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(reply.snippet, color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, lineHeight = 15.sp)

                        if (reply.suggestedNextAction.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFF2E93).copy(alpha = 0.15f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFF2E93), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Next Step: ${reply.suggestedNextAction}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedMemoryBankSection(viewModel: CrmViewModel) {
    val memories by viewModel.agentMemories.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SHARED LONG-TERM AGENT MEMORY BANK", color = Color(0xFFFF8A00), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("All 4 AI agents record insights and cross-learn to improve response rates daily.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }
            }
        }

        items(memories) { memory ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedAgentAvatar(agentType = memory.agentType, size = 28.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(memory.agentName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFF2E93).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(memory.category, color = Color(0xFFFF2E93), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(memory.content, color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, lineHeight = 15.sp)

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(memory.timestamp)),
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                        Row {
                            repeat(memory.impactRating) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgentLiveLogsSection(viewModel: CrmViewModel) {
    val logs by viewModel.agentLogs.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("LIVE MULTI-AGENT EXECUTION STREAM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            TextButton(onClick = { viewModel.clearAgentLogs() }) {
                Text("Clear Stream", color = Color(0xFFFF2E93), fontSize = 11.sp)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            items(logs) { log ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFFFF8A00), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(log.agentName, color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("• ${log.action}", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                            }
                            Text(log.details, color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmailTemplateEditor(viewModel: CrmViewModel) {
    val settings by viewModel.businessSettings.collectAsState()
    val context = LocalContext.current

    var subject by remember(settings) { mutableStateOf("Exploring Student Recruitment Partnership with [University Name]") }
    var body by remember(settings) {
        mutableStateOf(
            "Hi [Partnership Manager],\n\n" +
            "Hope you're having a productive week!\n\n" +
            "I'm ${settings.ceoName}, CEO of ${settings.agencyName}. We are a student recruitment consultancy specializing in connecting motivated students with welcoming, international student-friendly universities.\n\n" +
            "We admire your straightforward admission processes and would love to explore a commission-based B2B partnership. We handle end-to-end student vetting and visa guidance at no extra charge to students.\n\n" +
            "Are you open to onboarding new recruitment partners for upcoming intakes?\n\n" +
            "Best regards,\n\n" +
            "${settings.ceoName}\n" +
            "CEO, ${settings.agencyName}\n" +
            "WhatsApp: ${settings.whatsappNumber}"
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("B2B OUTREACH TEMPLATE EDITOR", color = Color(0xFFFF8A00), fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject Template") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = transparentTextFieldColors()
                    )

                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Body Template (Persuasive Human Tone)") },
                        minLines = 8,
                        modifier = Modifier.fillMaxWidth(),
                        colors = transparentTextFieldColors()
                    )

                    Button(
                        onClick = {
                            viewModel.saveBusinessSettings(settings.copy(emailSignature = body))
                            Toast.makeText(context, "Template saved for Gabriel AI agent!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E93))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Agent Master Template")
                    }
                }
            }
        }
    }
}
