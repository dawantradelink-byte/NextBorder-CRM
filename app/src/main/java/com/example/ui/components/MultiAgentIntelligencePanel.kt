package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CrmViewModel
import com.example.viewmodel.ExecutiveAgentItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiAgentIntelligencePanel(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val executiveAgents by viewModel.executiveAgents.collectAsState()
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var agentSearchQuery by remember { mutableStateOf("") }

    // Filter agents with derivedStateOf optimization
    val filteredAgents by remember(executiveAgents, selectedCategoryFilter, agentSearchQuery) {
        derivedStateOf {
            executiveAgents.filter { agent ->
                val matchesQuery = agentSearchQuery.isBlank() ||
                    agent.name.contains(agentSearchQuery, ignoreCase = true) ||
                    agent.roleDescription.contains(agentSearchQuery, ignoreCase = true) ||
                    agent.category.contains(agentSearchQuery, ignoreCase = true)

                val matchesCategory = if (selectedCategoryFilter == "All") true else agent.category.equals(selectedCategoryFilter, ignoreCase = true)

                matchesQuery && matchesCategory
            }
        }
    }

    val totalCompletedTasks = executiveAgents.sumOf { it.completedCount }
    val totalPendingTasks = executiveAgents.sumOf { it.pendingCount }
    val avgConfidence = if (executiveAgents.isNotEmpty()) executiveAgents.map { it.confidenceScore }.average().toInt() else 97

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
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
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("20 MULTI-AGENT INTELLIGENCE ARCHITECTURE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF7C3AED).copy(alpha = 0.25f)) {
                                Text("PHASE 17", color = Color(0xFF7C3AED), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Gemini Central AI Brain • Autonomous Task Coordination", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = { viewModel.runMultiAgentSystemCoordination() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync 20 Agents", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // Central AI Brain Status Banner
            Surface(
                color = Color.Black.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                            Text("CENTRAL AI BRAIN: GEMINI 1.5 PRO / FLASH ENGINE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF10B981)) {
                            Text("100% OPERATIONAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Text(
                        "All 20 specialized AI sub-agents report strictly to CEO AI (Master Coordinator). Direct inter-agent communication is disabled to guarantee zero duplicate execution and single-source executive decision integrity.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AgentSummaryMetric("Active Agents", "${executiveAgents.size}/20", Color(0xFF7C3AED))
                        AgentSummaryMetric("Completed Tasks", "$totalCompletedTasks", Color(0xFF10B981))
                        AgentSummaryMetric("Pending Queue", "$totalPendingTasks", Color(0xFFFF8A00))
                        AgentSummaryMetric("Avg Confidence", "$avgConfidence%", Color(0xFF00AEEF))
                    }
                }
            }

            // Category Filter Chips & Search
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = agentSearchQuery,
                    onValueChange = { agentSearchQuery = it },
                    placeholder = { Text("Filter agent by name, role, or category...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7C3AED),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val catFilters = listOf("All", "Executive Coordination", "Research", "Financial", "Admissions", "Compliance", "Communication", "Growth", "Operations", "Intelligence", "System")
                    items(catFilters.size) { idx ->
                        val catOpt = catFilters[idx]
                        FilterChip(
                            selected = selectedCategoryFilter == catOpt,
                            onClick = { selectedCategoryFilter = catOpt },
                            label = { Text(catOpt, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF7C3AED),
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 20 Agents Cards Grid / List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("SPECIALIZED AGENT NETWORK (${filteredAgents.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                filteredAgents.forEach { agent ->
                    AgentStatusCard(agent = agent)
                }
            }
        }
    }
}

@Composable
fun AgentStatusCard(agent: ExecutiveAgentItem) {
    Surface(
        color = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(shape = CircleShape, color = Color(0xFF7C3AED).copy(alpha = 0.2f), modifier = Modifier.size(24.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(agent.id, color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                    Text(agent.name, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (agent.status) {
                        "Active" -> Color(0xFF10B981)
                        "Running" -> Color(0xFFFF8A00)
                        else -> Color(0xFF6B7280)
                    }
                ) {
                    Text(agent.status, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Text(agent.roleDescription, color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)

            // Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Category: ${agent.category}", color = Color(0xFF00AEEF), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    Text("•", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    Text("Done: ${agent.completedCount}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("•", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    Text("Pending: ${agent.pendingCount}", color = Color(0xFFFF8A00), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Text("Confidence: ${agent.confidenceScore}%", color = Color(0xFFA78BFA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Last Action: ${agent.lastAction}", color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
fun AgentSummaryMetric(label: String, value: String, color: Color) {
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
            Text(value, color = color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
