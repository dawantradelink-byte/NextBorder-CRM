package com.example.ui.screens

import com.example.ui.components.FounderCeoCommandCenter
import com.example.ui.components.PersonalProductivityPanel
import com.example.modules.research.ui.StudentMatchingPanel
import com.example.ui.components.ExecutiveMemoryTimelinePanel
import com.example.ui.components.MultiAgentIntelligencePanel
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TodoItem
import com.example.data.University
import com.example.ui.components.*
import com.example.util.CsvUtils
import com.example.util.IntentUtils
import com.example.util.UniversityStatusEngine
import com.example.ui.components.MiniCalendar
import com.example.ui.components.NextBorderLogo
import com.example.ui.components.TodoListSection
import com.example.viewmodel.CrmViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DashboardScreen(
    viewModel: CrmViewModel,
    onNavigateToUniversity: (Int?) -> Unit,
    onNavigateToAgents: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalytics: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(2) } // Default to CRM tab

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            FloatingTabsBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToAgents = onNavigateToAgents,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Crossfade(targetState = selectedTab, label = "ScreenTransition") { tab ->
                when (tab) {
                    0 -> WelcomeScreen(onStart = { selectedTab = 2 })
                    1 -> ConfigQuickSettingsScreen(viewModel, onNavigateToSettings)
                    2 -> CrmDashboardScreen(viewModel, onNavigateToUniversity, onNavigateToAnalytics, onNavigateToAgents)
                    3 -> RoadmapScreen()
                    4 -> AgentQuickLaunchScreen(onNavigateToAgents)
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NEXT BORDER",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    ),
                    letterSpacing = 2.sp
                )
                Text(
                    text = "VISA & RECRUITMENT CONSULTANCY",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFF8A00)
                    ),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.scale(scale)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF3B1C58), Color(0xFF150B2E))
                            )
                        )
                        .border(
                            2.dp,
                            Brush.linearGradient(listOf(Color(0xFFFF8A00), Color(0xFFFF2E93))),
                            RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    NextBorderLogo()
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "B2B UNIVERSITY PARTNERSHIP\n& STUDENT RECRUITMENT CRM",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF8A00), Color(0xFFFF2E93))
                            )
                        )
                        .clickable { onStart() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OPEN CRM DASHBOARD",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ConfigQuickSettingsScreen(viewModel: CrmViewModel, onFullSettings: () -> Unit) {
    val settings by viewModel.businessSettings.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("AGENCY IDENTITY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Agency Name: ${settings.agencyName}", color = Color.White.copy(alpha = 0.9f))
                    Text("Managing Director: ${settings.ceoName}", color = Color.White.copy(alpha = 0.9f))
                    Text("WhatsApp: ${settings.whatsappNumber}", color = Color.White.copy(alpha = 0.9f))
                    Text("Website: ${settings.website}", color = Color.White.copy(alpha = 0.9f))

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onFullSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00))
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Full Agency Settings")
                    }
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("DEFAULT RECRUITMENT INTAKES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    val months = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (i in 0 until 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (j in 0 until 4) {
                                    val month = months[i * 4 + j]
                                    val isActive = month == "JAN" || month == "MAY" || month == "SEP"
                                    MonthPill(month, isActive)
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
fun MonthPill(month: String, isActive: Boolean) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .then(
                if (isActive) {
                    Modifier.background(Brush.horizontalGradient(listOf(Color(0xFFFF8A00), Color(0xFFFF2E93))))
                } else {
                    Modifier.background(Color.White.copy(alpha = 0.1f))
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = month,
            color = if (isActive) Color.White else Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun CrmDashboardScreen(
    viewModel: CrmViewModel,
    onNavigateToUniversity: (Int?) -> Unit,
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToAgents: () -> Unit = {}
) {
    val context = LocalContext.current
    val filteredUniversities by viewModel.filteredUniversities.collectAsState()
    val allUniversities by viewModel.activeUniversities.collectAsState()
    val todos by viewModel.todos.collectAsState()
    val meetings by viewModel.allMeetings.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val countryFilter by viewModel.countryFilter.collectAsState()
    val priorityFilter by viewModel.priorityFilter.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    val total = allUniversities.size
    val partnered = allUniversities.count { it.partnershipStatus == "Partnered" || it.status == "Partnered" }
    val contacted = allUniversities.count { it.status != "New" && it.partnershipStatus != "Prospect" }
    val highPriority = allUniversities.count { it.priority == "High" }
    val pendingTodos = todos.count { !it.isCompleted }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp)
    ) {
        // Phase 7 Enterprise Executive Lucid Glass Header Banner
        item {
            ExecutiveHeaderBanner()
        }

        // Phase 10 Autonomous Business Intelligence Founder CEO Command Center
        item {
            FounderCeoCommandCenter(
                viewModel = viewModel,
                onNavigateToAgents = { onNavigateToAgents() },
                onNavigateToAnalytics = { onNavigateToAnalytics() }
            )
        }

        // Phase 8 Personal AI Assistant & WorkManager Productivity Panel
        item {
            PersonalProductivityPanel(viewModel = viewModel)
        }

        // Phase 14 Student Matching & Opportunity Intelligence System
        item {
            StudentMatchingPanel(viewModel = viewModel)
        }

        // Phase 16 Executive Memory & Partnership Intelligence Timeline
        item {
            ExecutiveMemoryTimelinePanel(viewModel = viewModel)
        }

        // Phase 17 Gemini Executive Assistant & 20 Multi-Agent Intelligence
        item {
            MultiAgentIntelligencePanel(viewModel = viewModel)
        }

        // Phase 7 CEO AI Executive Health & Recommendation Panel
        item {
            CeoExecutivePanel()
        }

        // Phase 7 Autonomous AI Agent Cards Grid
        item {
            AiAgentCardsPanel(onNavigateToAgents = { })
        }

        // Phase 7 University Research & Fee Waiver Opportunities Panel
        item {
            UniversityResearchPanel()
        }

        // Phase 7 Partnership Conversion Pipeline Panel
        item {
            PartnershipPipelinePanel()
        }

        // Phase 7 Email Outreach & Follow-Up Queue Panel
        item {
            EmailOutreachPanel()
        }

        // Phase 7 Google Sheets Real-Time Sync Panel
        item {
            GoogleSheetsPanel()
        }

        // Phase 7 AI Event Timeline & Notification Center
        item {
            NotificationCenterPanel()
        }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NextBorderLogo()
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onNavigateToAnalytics) {
                            Surface(
                                color = Color(0xFF3B82F6),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Analytics, contentDescription = "Analytics Dashboard", tint = Color.White)
                                }
                            }
                        }
                        IconButton(onClick = { onNavigateToUniversity(null) }) {
                            Surface(
                                color = Color(0xFFFF8A00),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = "Add University", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }

        // Summary Metrics Cards
        item {
            val totalCount = allUniversities.size
            val activeCount = allUniversities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "ACTIVE" }
            val partnerCount = allUniversities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "PARTNER" }
            val greylistCount = allUniversities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "GREYLIST" }
            val blacklistCount = allUniversities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "BLACKLIST" }
            val meetingCount = allUniversities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "MEETING" }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item { StatCard("Total Leads", totalCount.toString(), isPrimary = true) }
                item { StatCard("Active Leads", activeCount.toString()) }
                item { StatCard("Partners", partnerCount.toString()) }
                item { StatCard("Meetings", meetingCount.toString()) }
                item { StatCard("Greylisted", greylistCount.toString()) }
                item { StatCard("Blacklisted", blacklistCount.toString()) }
            }
        }

        // Elite Marketing Strategy & AI Pipeline Operations Card
        item {
            var showProposalDialog by remember { mutableStateOf(false) }
            var proposedActionTitle by remember { mutableStateOf("") }
            var proposedActionDescription by remember { mutableStateOf("") }
            var proposedActionCallback by remember { mutableStateOf<(() -> Unit)?>(null) }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFFFF8A00))
                            Column {
                                Text("Visionary Marketing Strategy Department", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Real-Time Global Visa Intelligence • Autonomous AI Agents", color = Color(0xFF10B981), fontSize = 10.sp)
                            }
                        }
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFF2E93).copy(alpha = 0.2f)) {
                            Text("USER PERMISSION ENFORCED", color = Color(0xFFFF2E93), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val totalD = allUniversities.size.toDouble().coerceAtLeast(1.0)
                            val partnersN = allUniversities.count { UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "PARTNER" }
                            val rejN = allUniversities.count { 
                                val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
                                s == "GREYLIST" || s == "BLACKLIST"
                            }
                            val acceptancePct = (partnersN / totalD) * 100
                            val rejectionPct = (rejN / totalD) * 100

                            Text("Partnership Acceptance: ${String.format("%.1f%%", acceptancePct)}", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Rejection & Greylist Rate: ${String.format("%.1f%%", rejectionPct)}", color = Color(0xFFF59E0B), fontSize = 11.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = onNavigateToAnalytics,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E93)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.syncLeadsToGoogleSheets(context)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync 12 Sheets", fontSize = 11.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                proposedActionTitle = "Propose Greylist Re-Evaluation Campaign"
                                proposedActionDescription = "Analyze all Greylisted universities and propose scheduled follow-up pitches using updated CEO Ishak Dewan credentials and South Asia student cohort metrics."
                                proposedActionCallback = {
                                    Toast.makeText(context, "Executing Greylist Strategy Re-evaluation!", Toast.LENGTH_SHORT).show()
                                }
                                showProposalDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Propose Greylist Sweep", fontSize = 10.sp, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                proposedActionTitle = "Propose B2B Outreach Scaling (UK & Europe)"
                                proposedActionDescription = "Scale high-conversion email outreach targeting 10 new pre-screened UK and European universities for upcoming January/September intakes."
                                proposedActionCallback = {
                                    Toast.makeText(context, "Executing Scaling Proposal!", Toast.LENGTH_SHORT).show()
                                }
                                showProposalDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Propose Scaling Action", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }

                // Explicit User Confirmation Dialog
                if (showProposalDialog) {
                    AlertDialog(
                        onDismissRequest = { showProposalDialog = false },
                        containerColor = Color(0xFF1F2937),
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFFF8A00))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(proposedActionTitle, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(proposedActionDescription, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                                Text("As per strict system security guidelines, explicit user confirmation is required before performing strategic batch operations.", color = Color(0xFF10B981), fontSize = 11.sp)
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    proposedActionCallback?.invoke()
                                    showProposalDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00))
                            ) {
                                Text("Authorize Execution", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showProposalDialog = false }) {
                                Text("Decline / Cancel", color = Color.White)
                            }
                        }
                    )
                }
            }
        }

        // Web Admin Laptop Dashboard Control Card
        item {
            val webUrl by viewModel.webServerUrl.collectAsState()
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = if (webUrl != null) Color(0xFF10B981) else Color(0xFFFF8A00),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Computer,
                                        contentDescription = "Laptop Web Dashboard",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Laptop Web Admin Dashboard",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (webUrl != null) "Server Running • Live on Wi-Fi" else "Manage outreach & university leads from any Laptop browser",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleWebAdminServer(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (webUrl != null) Color(0xFFEF4444) else Color(0xFFFF2E93)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(if (webUrl != null) "Stop Server" else "Start Server")
                        }
                    }

                    if (webUrl != null) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Laptop Browser URL:", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = webUrl!!,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Web Admin URL", webUrl)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied Laptop URL to Clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFFFF8A00))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy URL", color = Color(0xFFFF8A00), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search Bar & Filter Chips
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search by university, contact, country, tags...", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = Color.White)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF2E93),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    // Filter Status Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val statuses = listOf("All", "Prospect", "Contacted", "In Discussion", "Partnered", "On Hold")
                        items(statuses) { st ->
                            FilterChip(
                                selected = statusFilter == st,
                                onClick = { viewModel.setStatusFilter(st) },
                                label = { Text(st) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFF2E93),
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    labelColor = Color.White,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Filter Country Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Country:", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(end = 6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val countries = listOf("All", "United Kingdom", "USA", "Canada", "Italy", "Hungary", "Germany", "Australia")
                            items(countries) { c ->
                                FilterChip(
                                    selected = countryFilter == c,
                                    onClick = { viewModel.setCountryFilter(c) },
                                    label = { Text(c, fontSize = 11.sp) },
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

                    // Sort dropdown row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sort By:", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val sortOpts = listOf("Newest", "Name", "Priority", "Last Contacted")
                            items(sortOpts) { opt ->
                                FilterChip(
                                    selected = sortBy == opt,
                                    onClick = { viewModel.setSortBy(opt) },
                                    label = { Text(opt, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFF8A00),
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                        labelColor = Color.White,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            MiniCalendar(universities = allUniversities)
        }

        item {
            TodoListSection(
                todos = todos,
                onAddTodo = { taskText, dueDate, priority ->
                    viewModel.addTodo(taskText, dueDate, priority)
                },
                onToggleTodo = { todo ->
                    viewModel.toggleTodoCompleted(todo)
                },
                onDeleteTodo = { todo ->
                    viewModel.deleteTodo(todo)
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "University Partners (${filteredUniversities.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val listToExport = if (filteredUniversities.isNotEmpty()) filteredUniversities else allUniversities
                            val csvFile = CsvUtils.exportUniversitiesToCsv(context, listToExport)
                            if (csvFile != null) {
                                IntentUtils.shareFile(context, csvFile, "Export University Contacts for Email Marketing")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export CSV", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export Contacts CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = { onNavigateToUniversity(null) }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFFF8A00))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New", color = Color(0xFFFF8A00))
                    }
                }
            }
        }

        if (filteredUniversities.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No matching universities found.", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigateToUniversity(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00))
                        ) {
                            Text("Add First University Partner")
                        }
                    }
                }
            }
        } else {
            itemsIndexed(filteredUniversities) { index, uni ->
                UniversityListItemCard(
                    university = uni,
                    onClick = { onNavigateToUniversity(uni.id) },
                    onEmailClick = {
                        IntentUtils.launchEmail(context, uni.email, "Student Recruitment Partnership with Next Border", "")
                    },
                    onWhatsAppClick = {
                        IntentUtils.launchWhatsApp(context, uni.whatsappNumber, "Hello ${uni.contactName}, writing from Next Border Consultancy...")
                    },
                    onCalendarClick = {
                        IntentUtils.launchGoogleCalendar(context, "Call with ${uni.name}", "Discuss student recruitment partnership")
                    }
                )
            }
        }
    }
}

@Composable
fun UniversityListItemCard(
    university: University,
    onClick: () -> Unit,
    onEmailClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        university.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${university.country} • ${if (university.contactName.isNotBlank()) university.contactName else "Admissions Director"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = when (university.partnershipStatus) {
                        "Partnered" -> Color(0xFF10B981)
                        "In Discussion" -> Color(0xFFFF8A00)
                        "Contacted" -> Color(0xFF3B82F6)
                        else -> Color(0xFFFF2E93)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        university.partnershipStatus,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Intake: ${university.intakeMonths} • Comm: ${university.commissionRate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

                // Quick Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (university.email.isNotBlank()) {
                        IconButton(onClick = onEmailClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFFFF8A00), modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(onClick = onWhatsAppClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "WhatsApp", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onCalendarClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = Color(0xFFFF2E93), modifier = Modifier.size(18.dp))
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp).align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, isPrimary: Boolean = false) {
    GlassCard(
        modifier = modifier.width(100.dp).height(85.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isPrimary) Color(0xFFFF8A00) else Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AgentQuickLaunchScreen(onNavigateToAgents: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFFFF2E93), modifier = Modifier.size(48.dp))
                Text("GABRIEL AI AGENT MANAGER", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Manage parallel AI agents, automated email drafting, and university recruitment intelligence.", color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)

                Button(
                    onClick = onNavigateToAgents,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E93))
                ) {
                    Text("Launch AI Agent Control Hub")
                }
            }
        }
    }
}

@Composable
fun RoadmapScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "NEXT BORDER CRM ROADMAP",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    item {
                        RoadmapStepItem("PHASE 01", "B2B Partnership Outreach", "University database, automated email generator, and contact history.", Color(0xFFFF2E93))
                    }
                    item {
                        RoadmapStepItem("PHASE 02", "Student Lead Pipeline", "Link student applicants with partnered universities and track commission status.", Color(0xFFFF8A00))
                    }
                    item {
                        RoadmapStepItem("PHASE 03", "AI Visa Eligibility Audit", "Automated MOI & English proficiency verification for UK/EU universities.", Color(0xFFFF2E93))
                    }
                    item {
                        RoadmapStepItem("PHASE 04", "Multi-Agent Gabriel Sync", "Continuous scraping and updates for university application deadlines and fee changes.", Color(0xFFFF8A00))
                    }
                }
            }
        }
    }
}

@Composable
fun RoadmapStepItem(stepNumber: String, title: String, description: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(colors = listOf(color, color.copy(alpha = 0.2f)))),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(stepNumber, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                description,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun FloatingTabsBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToAgents: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .shadow(16.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF1F1147).copy(alpha = 0.9f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(32.dp))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf(
                Icons.Default.Home to "Welcome",
                Icons.Default.Settings to "Settings",
                Icons.Default.Dashboard to "CRM",
                Icons.AutoMirrored.Filled.List to "Roadmap",
                Icons.Default.SmartToy to "Agents"
            )

            tabs.forEachIndexed { index, (icon, label) ->
                TabItem(
                    icon = icon,
                    label = label,
                    isSelected = selectedTab == index,
                    onClick = {
                        if (index == 4) {
                            onNavigateToAgents()
                        } else {
                            onTabSelected(index)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFFFF2E93) else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(22.dp)
        )
        AnimatedVisibility(visible = isSelected) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF2E93),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
