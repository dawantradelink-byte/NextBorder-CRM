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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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

            Spacer(modifier = Modifier.height(32.dp))

            // Test Statistics Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF232323))
                    .padding(20.dp)
                    .scale(scale)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Test Statistics", color = Color.White, fontSize = 16.sp)
                        Column(horizontalAlignment = Alignment.Start) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(Color(0xFFFFD54F), CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Performance", color = Color.White, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(Color.Gray, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Accuracy", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val yellowPath = Path().apply {
                                moveTo(0f, h * 0.7f)
                                cubicTo(w * 0.1f, h * 0.9f, w * 0.2f, h * 0.6f, w * 0.3f, h * 0.7f)
                                cubicTo(w * 0.4f, h * 0.8f, w * 0.45f, h * 0.5f, w * 0.5f, h * 0.6f)
                                cubicTo(w * 0.6f, h * 0.8f, w * 0.7f, h * 0.9f, w * 0.8f, h * 0.8f)
                                cubicTo(w * 0.9f, h * 0.7f, w * 0.95f, h * 0.4f, w, h * 0.1f)
                            }

                            val greyPath = Path().apply {
                                moveTo(0f, h * 0.5f)
                                cubicTo(w * 0.15f, h * 0.4f, w * 0.2f, h * 0.8f, w * 0.3f, h * 0.6f)
                                cubicTo(w * 0.4f, h * 0.4f, w * 0.5f, h * 0.9f, w * 0.6f, h * 0.7f)
                                cubicTo(w * 0.7f, h * 0.5f, w * 0.8f, h * 0.7f, w * 0.9f, h * 0.9f)
                            }

                            drawPath(
                                path = greyPath,
                                color = Color.Gray,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            )

                            drawPath(
                                path = yellowPath,
                                color = Color(0xFFFFD54F),
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Highlight Point
                            val pointX = w * 0.5f
                            val pointY = h * 0.6f

                            drawCircle(color = Color(0xFF232323), radius = 6.dp.toPx(), center = androidx.compose.ui.geometry.Offset(pointX, pointY))
                            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(pointX, pointY), style = Stroke(width = 2.dp.toPx()))
                            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(pointX, pointY))
                        }

                        // Tooltip
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(y = (-40).dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(Color(0xFFFFD54F), CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("278 points", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Q1", color = Color.Gray, fontSize = 12.sp)
                        Text("Q2", color = Color.Gray, fontSize = 12.sp)
                        Text("Q3", color = Color.Gray, fontSize = 12.sp)
                        Text("Q4", color = Color.Gray, fontSize = 12.sp)
                        Text("Q5", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFB19886))) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ishak_dawan),
            contentDescription = "Profile Background",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.TopCenter)
        )

        // Gradient overlay to blend with the bottom color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF8E7C6D).copy(alpha = 0.5f),
                            Color(0xFFB19886)
                        ),
                        startY = 0f,
                        endY = 1800f
                    )
                )
        )

        // Adding the required "3D Motion Graphics" using infinite transition for the Chart background and the image
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.98f,
            targetValue = 1.02f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                        .clickable { onStart() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = Color.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More",
                            tint = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Ishak Dawan",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                ),
                modifier = Modifier.scale(scale)
            )
            Text(
                text = "Fullstack Developer",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kotlin Tag
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF8B6C77).copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(12.dp).background(Brush.linearGradient(listOf(Color(0xFF8052FF), Color(0xFFFF7E42))), shape = CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kotlin", color = Color.White, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Firebase Tag
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF987B68).copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = "Firebase", tint = Color(0xFFFFCA28), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Firebase", color = Color.White, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Jetpack Compose Tag
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF757A82).copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(12.dp).background(Color(0xFF4285F4), shape = CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Jetpack Compose", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Building scalable Android applications with modern technologies and clean architecture. Passionate about creating efficient and user-friendly solutions.",
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Experience
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("Experience", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFD54F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("5.2 Years", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Projects
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("Projects", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF232323)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("48", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Clients (Striped background)
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("Clients", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val stripeWidth = 5.dp.toPx()
                            val spacing = 8.dp.toPx()
                            val width = size.width
                            val height = size.height
                            val max = width + height
                            var i = 0f
                            while(i < max) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.3f),
                                    start = androidx.compose.ui.geometry.Offset(i, 0f),
                                    end = androidx.compose.ui.geometry.Offset(i - height, height),
                                    strokeWidth = 1.dp.toPx()
                                )
                                i += spacing
                            }
                        }
                        Text("23", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Reviews
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("Reviews", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF9EA3AB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("4.9", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Star, contentDescription = "Star", tint = Color(0xFFFFCA28), modifier = Modifier.size(12.dp))
                        }
                    }
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
                                    Icon(Icons.Default.Clear, contentDescription = null, tint = Color.White)
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
