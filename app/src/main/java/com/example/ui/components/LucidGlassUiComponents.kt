package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExecutiveHeaderBanner(
    onOpenMenu: () -> Unit = {}
) {
    val currentTime = remember {
        val sdf = SimpleDateFormat("EEEE, MMM d • HH:mm", Locale.getDefault())
        sdf.format(Date())
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0x1A00AEEF),
        borderColor = AiosGlassBorder,
        cornerRadius = 24
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Modern Icon Logo Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AiosSecondary)
                            .border(1.dp, AiosPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.size(28.dp)) {
                            val w = size.width
                            val h = size.height
                            val shieldPath = androidx.compose.ui.graphics.Path().apply {
                                moveTo(w * 0.5f, h * 0.05f)
                                cubicTo(w * 0.75f, h * 0.05f, w * 0.95f, h * 0.15f, w * 0.95f, h * 0.35f)
                                cubicTo(w * 0.95f, h * 0.72f, w * 0.5f, h * 0.98f, w * 0.5f, h * 0.98f)
                                cubicTo(w * 0.5f, h * 0.98f, w * 0.05f, h * 0.72f, w * 0.05f, h * 0.35f)
                                cubicTo(w * 0.05f, h * 0.15f, w * 0.25f, h * 0.05f, w * 0.5f, h * 0.05f)
                                close()
                            }
                            drawPath(path = shieldPath, color = Color(0xFF00AEEF), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f))
                            val arrowPath = androidx.compose.ui.graphics.Path().apply {
                                moveTo(w * 0.35f, h * 0.60f)
                                lineTo(w * 0.35f, h * 0.45f)
                                lineTo(w * 0.55f, h * 0.45f)
                                lineTo(w * 0.55f, h * 0.35f)
                                lineTo(w * 0.75f, h * 0.50f)
                                lineTo(w * 0.55f, h * 0.65f)
                                lineTo(w * 0.55f, h * 0.55f)
                                lineTo(w * 0.35f, h * 0.55f)
                                close()
                            }
                            drawPath(path = arrowPath, color = Color.White)
                        }
                    }

                    Column {
                        Text(
                            text = "NextBorder AIOS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Global Education • Visa • AI",
                            fontSize = 11.sp,
                            color = Color(0xFFC0C0C0),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // CEO Status Indicator
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AiosSecondary.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AiosSuccess.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AiosSuccess)
                        )
                        Text("CEO Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Quick Status Pill Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item { SystemStatusChip("CEO AI", "Active", AiosSuccess, Icons.Default.Psychology) }
                item { SystemStatusChip("Founder", "Ishak Dawan", Color(0xFF00AEEF), Icons.Default.Person) }
                item { SystemStatusChip("Company", "NextBorder Visa", AiosAccent, Icons.Default.Business) }
                item { SystemStatusChip("Gemini API", "3.5", AiosAccent, Icons.Default.AutoAwesome) }
                item { SystemStatusChip("WorkManager", "Running", AiosSuccess, Icons.Default.Sync) }
            }
        }
    }
}

@Composable
fun SystemStatusChip(label: String, value: String, statusColor: Color, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0x15FFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
            Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Text(value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun CeoExecutivePanel() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0x1FFFFFFF),
        borderColor = AiosGlassBorder,
        cornerRadius = 24
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = AiosAccent, modifier = Modifier.size(20.dp))
                    Text("AI CEO Command & Health Panel", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AiosSuccess.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AiosSuccess)
                ) {
                    Text(
                        "Health: 99.8%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AiosSuccess,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Metric Summary Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(modifier = Modifier.weight(1f), title = "Running", value = "18", subtitle = "Agents Active", color = AiosSuccess)
                MetricPill(modifier = Modifier.weight(1f), title = "Failed", value = "0", subtitle = "Failures", color = AiosPrimary)
                MetricPill(modifier = Modifier.weight(1f), title = "Pending", value = "0", subtitle = "Tasks", color = AiosAccent)
                MetricPill(modifier = Modifier.weight(1f), title = "Efficiency", value = "100%", subtitle = "Nominal", color = AiosWarning)
            }

            // Daily CEO Recommendation Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AiosPrimary.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AiosPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AiosWarning, modifier = Modifier.size(24.dp))
                    Column {
                        Text("Daily CEO Recommendation", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AiosAccent)
                        Text(
                            "Focus partnership outreach on UK Russell Group & London Pathway Providers. Automate 3-day email follow-ups for maximum conversion.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricPill(modifier: Modifier = Modifier, title: String, value: String, subtitle: String, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0x10FFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(subtitle, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
        }
    }
}

data class AgentCardInfo(
    val name: String,
    val role: String,
    val status: String,
    val cpu: String,
    val memory: String,
    val tasks: Int,
    val lastExecution: String,
    val icon: ImageVector,
    val statusColor: Color = AiosSuccess
)

@Composable
fun AiAgentCardsPanel(
    onNavigateToAgents: () -> Unit = {}
) {
    val agentList = listOf(
        AgentCardInfo("University Research AI", "UK Higher Ed Scraper", "Active", "1.8%", "42MB", 124, "2m ago", Icons.Default.School),
        AgentCardInfo("Marketing AI", "Campaign Optimizer", "Active", "2.1%", "38MB", 89, "5m ago", Icons.Default.Campaign),
        AgentCardInfo("Lead Hunter AI", "UK Opportunity Scorer", "Active", "3.4%", "56MB", 156, "1m ago", Icons.Default.SavedSearch),
        AgentCardInfo("Email AI", "Personalized Drafter", "Active", "1.2%", "31MB", 92, "3m ago", Icons.Default.Email),
        AgentCardInfo("Meeting AI", "Scheduler & Sync", "Idle", "0.2%", "22MB", 45, "12m ago", Icons.Default.Event, AiosAccent),
        AgentCardInfo("Admission AI", "Student Eligibility", "Active", "1.5%", "35MB", 67, "8m ago", Icons.Default.AssignmentInd),
        AgentCardInfo("Visa AI", "UK CAS & Guidance", "Idle", "0.4%", "24MB", 51, "15m ago", Icons.Default.FlightTakeoff, AiosAccent),
        AgentCardInfo("Document AI", "Verification Engine", "Active", "2.8%", "49MB", 110, "4m ago", Icons.Default.Description),
        AgentCardInfo("Analytics AI", "Growth Predictor", "Active", "0.9%", "28MB", 210, "30s ago", Icons.Default.BarChart),
        AgentCardInfo("Background AI", "WorkManager Sync", "Active", "1.1%", "33MB", 340, "10s ago", Icons.Default.Layers)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = AiosPrimary, modifier = Modifier.size(20.dp))
                Text("Autonomous AI Agents (10 Active)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            TextButton(onClick = onNavigateToAgents) {
                Text("View All 18", color = AiosAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AiosAccent, modifier = Modifier.size(14.dp))
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(agentList) { agent ->
                GlassAgentCard(agent)
            }
        }
    }
}

@Composable
fun GlassAgentCard(agent: AgentCardInfo) {
    GlassCard(
        modifier = Modifier
            .width(200.dp)
            .clickable { },
        containerColor = Color(0x18FFFFFF),
        borderColor = AiosGlassBorder,
        cornerRadius = 18
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = AiosPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(agent.icon, contentDescription = null, tint = AiosAccent, modifier = Modifier.size(18.dp))
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = agent.statusColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, agent.statusColor)
                ) {
                    Text(
                        agent.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = agent.statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(agent.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(agent.role, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
            }

            Divider(color = Color.White.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("CPU", fontSize = 8.sp, color = Color.White.copy(alpha = 0.5f))
                    Text(agent.cpu, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column {
                    Text("RAM", fontSize = 8.sp, color = Color.White.copy(alpha = 0.5f))
                    Text(agent.memory, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column {
                    Text("Tasks", fontSize = 8.sp, color = Color.White.copy(alpha = 0.5f))
                    Text("${agent.tasks}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AiosAccent)
                }
            }
        }
    }
}

@Composable
fun UniversityResearchPanel() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0x15FFFFFF),
        borderColor = AiosGlassBorder,
        cornerRadius = 20
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.School, contentDescription = null, tint = AiosPrimary, modifier = Modifier.size(18.dp))
                    Text("UK University Research & Fee Waiver Opportunities", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UniResearchStatPill("New Today", "5", AiosSuccess, Modifier.weight(1f))
                UniResearchStatPill("Partners", "12", AiosPrimary, Modifier.weight(1f))
                UniResearchStatPill("Scholarships", "28", AiosWarning, Modifier.weight(1f))
                UniResearchStatPill("Fee Waivers", "8", AiosAccent, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun UniResearchStatPill(title: String, count: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0x10FFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
            Text(count, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun PartnershipPipelinePanel() {
    val stages = listOf(
        "Contacted" to "24",
        "Interested" to "11",
        "Meeting Scheduled" to "6",
        "Agreement Pending" to "3",
        "Active Partner" to "18"
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0x15FFFFFF),
        borderColor = AiosGlassBorder,
        cornerRadius = 20
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Handshake, contentDescription = null, tint = AiosSuccess, modifier = Modifier.size(18.dp))
                Text("Partnership Pipeline (62 Total)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(stages) { (stage, count) ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AiosSecondary.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AiosGlassBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(stage, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            Surface(
                                shape = CircleShape,
                                color = AiosPrimary
                            ) {
                                Text(
                                    count,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmailOutreachPanel() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0x15FFFFFF),
        borderColor = AiosGlassBorder,
        cornerRadius = 20
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = AiosAccent, modifier = Modifier.size(18.dp))
                Text("Email Outreach & Automated Follow-Up Queue", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutreachQueueChip("Drafts", "14", AiosAccent, Modifier.weight(1f))
                OutreachQueueChip("Ready", "8", AiosSuccess, Modifier.weight(1f))
                OutreachQueueChip("3D Follow", "5", AiosWarning, Modifier.weight(1f))
                OutreachQueueChip("7D Follow", "3", AiosWarning, Modifier.weight(1f))
                OutreachQueueChip("15D Follow", "2", AiosDanger, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun OutreachQueueChip(label: String, count: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0x10FFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f), maxLines = 1)
            Text(count, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun GoogleSheetsPanel() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0x15FFFFFF),
        borderColor = AiosGlassBorder,
        cornerRadius = 20
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.TableChart, contentDescription = null, tint = AiosSuccess, modifier = Modifier.size(24.dp))
                Column {
                    Text("Google Sheets Lead Sync", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("1,480 Rows Synced • Last: 2 mins ago", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AiosSuccess.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AiosSuccess)
            ) {
                Text(
                    "Sync Live",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AiosSuccess,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationCenterPanel() {
    val timeline = listOf(
        Triple("02m ago", "University Research AI discovered 3 new UK Pathway Providers", AiosAccent),
        Triple("15m ago", "Meeting AI scheduled partnership call with BPP University", AiosSuccess),
        Triple("45m ago", "Email AI prepared 5 personalized follow-up drafts", AiosPrimary),
        Triple("1h ago", "Google Sheets Sync complete (1,480 records)", AiosWarning)
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0x15FFFFFF),
        borderColor = AiosGlassBorder,
        cornerRadius = 20
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = AiosWarning, modifier = Modifier.size(18.dp))
                Text("AI Event Timeline & Live Activity Feed", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                timeline.forEach { (time, text, badgeColor) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Text(time, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                        Text(text, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun ExecutiveNavigationDrawerContent(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        "Dashboard" to Icons.Default.Dashboard,
        "Students" to Icons.Default.People,
        "Applications" to Icons.Default.Assignment,
        "Universities" to Icons.Default.School,
        "AI CEO" to Icons.Default.Shield,
        "AI Agents" to Icons.Default.SmartToy,
        "Partnerships" to Icons.Default.Handshake,
        "Meetings" to Icons.Default.Event,
        "Leads" to Icons.Default.SavedSearch,
        "Documents" to Icons.Default.Description,
        "Analytics" to Icons.Default.BarChart,
        "Reports" to Icons.Default.Assessment,
        "Notifications" to Icons.Default.Notifications,
        "Messages" to Icons.AutoMirrored.Filled.Send,
        "Google Sheets" to Icons.Default.TableChart,
        "Settings" to Icons.Default.Settings
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(AiosSurface)
            .border(androidx.compose.foundation.BorderStroke(1.dp, AiosGlassBorder))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = AiosPrimary,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
            Column {
                Text("NextBorder AIOS", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text("Enterprise AI Platform", fontSize = 11.sp, color = AiosAccent)
            }
        }

        Divider(color = Color.White.copy(alpha = 0.15f))

        LazyRow(modifier = Modifier.weight(1f)) {
            // Placeholder list using LazyColumn structure in drawer content
        }
    }
}
