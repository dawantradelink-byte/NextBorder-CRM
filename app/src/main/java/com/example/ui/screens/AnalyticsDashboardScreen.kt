package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.University
import com.example.ui.components.GlassCard
import com.example.util.CsvUtils
import com.example.util.IntentUtils
import com.example.util.PdfReportExporter
import com.example.util.UniversityStatusEngine
import com.example.viewmodel.CrmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    viewModel: CrmViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val allUniversities by viewModel.activeUniversities.collectAsState()
    val meetings by viewModel.allMeetings.collectAsState()

    // Calculate Key Metrics
    val totalLeads = allUniversities.size
    val safeTotal = totalLeads.toDouble().coerceAtLeast(1.0)

    val partnerCount = allUniversities.count { 
        val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
        s == "PARTNER" || s == "COMPLETED"
    }
    val activeCount = allUniversities.count { 
        val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
        s == "ACTIVE"
    }
    val contactedCount = allUniversities.count { 
        val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
        s == "CONTACTED"
    }
    val followUpCount = allUniversities.count { 
        val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
        s == "FOLLOW_UP" || it.nextFollowUpDate > 0L
    }
    val meetingCount = allUniversities.count { 
        val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
        s == "MEETING"
    }
    val noResponseCount = allUniversities.count { 
        val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
        s == "NO_RESPONSE"
    }
    val greylistCount = allUniversities.count { 
        val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
        s == "GREYLIST"
    }
    val blacklistCount = allUniversities.count { 
        val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
        s == "BLACKLIST"
    }

    val acceptanceRate = (partnerCount / safeTotal) * 100.0
    val rejectionRate = ((greylistCount + blacklistCount) / safeTotal) * 100.0
    val inProgressRate = ((contactedCount + followUpCount + meetingCount) / safeTotal) * 100.0

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "CRM Analytics & KPI Intelligence",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            "Partnership Acceptance, Pipeline & Rejection Insights",
                            color = Color(0xFFFF8A00),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val file = CsvUtils.exportUniversitiesToCsv(context, allUniversities)
                            if (file != null) {
                                IntentUtils.shareFile(context, file, "Export Analytics CSV Report")
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = "Export CSV",
                            tint = Color(0xFF10B981)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Hero KPI Cards (Acceptance Rate, Pipeline Density, Rejection Ratio)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // KPI 1: Acceptance Rate Circular Indicator Card
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "ACCEPTANCE RATE",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(76.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 8.dp.toPx()
                                    // Background track
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.1f),
                                        style = Stroke(width = strokeWidth)
                                    )
                                    // Arc
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            listOf(Color(0xFF10B981), Color(0xFF3B82F6))
                                        ),
                                        startAngle = -90f,
                                        sweepAngle = (acceptanceRate * 3.6).toFloat().coerceIn(0f, 360f),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${String.format("%.1f", acceptanceRate)}%",
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        "$partnerCount Signed",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "Target: 20%",
                                    color = Color(0xFF10B981),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // KPI 2: Pipeline In-Progress Card
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "IN-PROGRESS LEADS",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(76.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 8.dp.toPx()
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.1f),
                                        style = Stroke(width = strokeWidth)
                                    )
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            listOf(Color(0xFFFF8A00), Color(0xFFFF2E93))
                                        ),
                                        startAngle = -90f,
                                        sweepAngle = (inProgressRate * 3.6).toFloat().coerceIn(0f, 360f),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${contactedCount + followUpCount + meetingCount}",
                                        color = Color(0xFFFF8A00),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        "Active Pitch",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFF8A00).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "${meetingCount} Meetings",
                                    color = Color(0xFFFF8A00),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // KPI 3: Rejection & Greylist Ratio
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "REJECTION RATIO",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(76.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 8.dp.toPx()
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.1f),
                                        style = Stroke(width = strokeWidth)
                                    )
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
                                        ),
                                        startAngle = -90f,
                                        sweepAngle = (rejectionRate * 3.6).toFloat().coerceIn(0f, 360f),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${String.format("%.1f", rejectionRate)}%",
                                        color = Color(0xFFF59E0B),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        "${greylistCount + blacklistCount} Refusals",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "$greylistCount Greylist",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Pipeline Visual Breakdown Chart
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFFFF8A00))
                                Text(
                                    "Current Pipeline Status Breakdown",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                "$totalLeads Total Leads",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }

                        // Custom Segmented Horizontal Bar Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                if (activeCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(activeCount.toFloat())
                                            .fillMaxHeight()
                                            .background(Color(0xFF3B82F6))
                                    )
                                }
                                if (contactedCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(contactedCount.toFloat())
                                            .fillMaxHeight()
                                            .background(Color(0xFF8B5CF6))
                                    )
                                }
                                if (followUpCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(followUpCount.toFloat())
                                            .fillMaxHeight()
                                            .background(Color(0xFFFF2E93))
                                    )
                                }
                                if (meetingCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(meetingCount.toFloat())
                                            .fillMaxHeight()
                                            .background(Color(0xFFFF8A00))
                                    )
                                }
                                if (partnerCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(partnerCount.toFloat())
                                            .fillMaxHeight()
                                            .background(Color(0xFF10B981))
                                    )
                                }
                                if (greylistCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(greylistCount.toFloat())
                                            .fillMaxHeight()
                                            .background(Color(0xFFF59E0B))
                                    )
                                }
                                if (blacklistCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(blacklistCount.toFloat())
                                            .fillMaxHeight()
                                            .background(Color(0xFFEF4444))
                                    )
                                }
                            }
                        }

                        // Detail Status Items List
                        val statusItems = listOf(
                            StatusMetricItem("1. ACTIVE (Qualified)", activeCount, totalLeads, Color(0xFF3B82F6), Icons.Default.CheckCircle),
                            StatusMetricItem("2. CONTACTED (Outreach Done)", contactedCount, totalLeads, Color(0xFF8B5CF6), Icons.Default.Email),
                            StatusMetricItem("3. FOLLOW_UP (Scheduled)", followUpCount, totalLeads, Color(0xFFFF2E93), Icons.Default.NotificationsActive),
                            StatusMetricItem("4. MEETING (Discussion)", meetingCount, totalLeads, Color(0xFFFF8A00), Icons.Default.Groups),
                            StatusMetricItem("5. PARTNER (Signed B2B)", partnerCount, totalLeads, Color(0xFF10B981), Icons.Default.Verified),
                            StatusMetricItem("6. GREYLIST (Temp Refusal)", greylistCount, totalLeads, Color(0xFFF59E0B), Icons.Default.HourglassTop),
                            StatusMetricItem("7. BLACKLIST (Permanent Stop)", blacklistCount, totalLeads, Color(0xFFEF4444), Icons.Default.Block),
                            StatusMetricItem("8. NO_RESPONSE (Awaiting)", noResponseCount, totalLeads, Color(0xFF9CA3AF), Icons.Default.MarkUnreadChatAlt)
                        )

                        statusItems.forEach { item ->
                            StatusProgressBarRow(item)
                        }
                    }
                }
            }

            // Section 3: Common Rejection Reasons Visualization
            item {
                var selectedRefusalTab by remember { mutableIntStateOf(0) } // 0: Greylist (Temp), 1: Blacklist (Perm)

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFFFF2E93))
                                Text(
                                    "Common Rejection Reasons & AI Recovery",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        // Tab Selection: Greylist vs Blacklist
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedRefusalTab == 0,
                                onClick = { selectedRefusalTab = 0 },
                                label = { Text("Temporary Refusals (Greylist - $greylistCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFF59E0B),
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    labelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = selectedRefusalTab == 1,
                                onClick = { selectedRefusalTab = 1 },
                                label = { Text("Permanent Stops (Blacklist - $blacklistCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEF4444),
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    labelColor = Color.White
                                )
                            )
                        }

                        if (selectedRefusalTab == 0) {
                            // Greylist Reasons
                            val greylistList = allUniversities.filter { 
                                UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "GREYLIST" 
                            }

                            if (greylistList.isEmpty()) {
                                Column(
                                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No Greylisted universities recorded.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                                    Text("When a university replies 'Not accepting agents', they will appear here.", color = Color(0xFF10B981), fontSize = 11.sp, textAlign = TextAlign.Center)
                                }
                            } else {
                                greylistList.forEach { u ->
                                    RejectionReasonCard(
                                        title = u.name,
                                        country = u.country,
                                        reason = u.greylistReason.ifBlank { "General temporary capacity limit" },
                                        strategy = u.greylistRecommendedStrategy.ifBlank { "Re-engage in 6 months with updated CEO profile and student volume." },
                                        followUpDate = if (u.greylistSuggestedFollowUpDate > 0) java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(u.greylistSuggestedFollowUpDate)) else "In 6 Months",
                                        accentColor = Color(0xFFF59E0B)
                                    )
                                }
                            }
                        } else {
                            // Blacklist Reasons
                            val blacklistList = allUniversities.filter { 
                                UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus) == "BLACKLIST" 
                            }

                            if (blacklistList.isEmpty()) {
                                Column(
                                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No Blacklisted universities recorded.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                                    Text("Universities requesting permanent removal will be listed here and blocked.", color = Color(0xFF10B981), fontSize = 11.sp, textAlign = TextAlign.Center)
                                }
                            } else {
                                blacklistList.forEach { u ->
                                    RejectionReasonCard(
                                        title = u.name,
                                        country = u.country,
                                        reason = u.blacklistReason.ifBlank { "Explicit permanent refusal or legal compliance request" },
                                        strategy = "STRICT BLOCK: Do not email, call, or schedule follow-ups. Maintained in database to prevent re-contact.",
                                        followUpDate = "PERMANENT BLOCK",
                                        accentColor = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Geographic / Country Performance Breakdown
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF3B82F6))
                                Text(
                                    "Target Country Performance Breakdown",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        val countryGroups = allUniversities.groupBy { it.country.ifBlank { "Unassigned" } }
                        
                        countryGroups.forEach { (country, unis) ->
                            val cTotal = unis.size
                            val cPartners = unis.count { 
                                val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
                                s == "PARTNER" || s == "COMPLETED"
                            }
                            val cMeetings = unis.count { 
                                val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
                                s == "MEETING"
                            }
                            val cGreylist = unis.count { 
                                val s = UniversityStatusEngine.mapToStandardStatus(it.partnershipStatus)
                                s == "GREYLIST"
                            }
                            val cRate = (cPartners.toDouble() / cTotal.toDouble().coerceAtLeast(1.0)) * 100.0

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        country,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        "$cPartners Partners / $cTotal Total ($cRate%)",
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { (cPartners.toFloat() / cTotal.toFloat()).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF10B981),
                                    trackColor = Color.White.copy(alpha = 0.1f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("$cMeetings Meetings Scheduled", color = Color(0xFFFF8A00), fontSize = 10.sp)
                                    Text("$cGreylist Greylisted Leads", color = Color(0xFFF59E0B), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class StatusMetricItem(
    val title: String,
    val count: Int,
    val total: Int,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun StatusProgressBarRow(item: StatusMetricItem) {
    val pct = if (item.total > 0) (item.count.toDouble() / item.total.toDouble()) * 100.0 else 0.0
    val progressFloat = (pct / 100.0).toFloat().coerceIn(0f, 1f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(16.dp))
                Text(
                    item.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                "${item.count} (${String.format("%.1f", pct)}%)",
                color = item.color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LinearProgressIndicator(
            progress = { progressFloat },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = item.color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun RejectionReasonCard(
    title: String,
    country: String,
    reason: String,
    strategy: String,
    followUpDate: String,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = accentColor.copy(alpha = 0.2f)
            ) {
                Text(
                    country,
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Text(
            "Reason: $reason",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            "AI Strategy: $strategy",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 10.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "Review Date: $followUpDate",
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
