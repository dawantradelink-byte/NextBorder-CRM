package com.example.modules.research.ui

import androidx.compose.animation.*
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
import com.example.modules.research.engine.ResearchAgentType
import com.example.modules.research.model.ResearchJobEntity
import com.example.modules.research.model.ResearchedInstitutionEntity
import com.example.ui.components.GlassCard
import com.example.viewmodel.CrmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResearchIntelligencePanel(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val researchedInstitutions by viewModel.researchedInstitutions.collectAsState()
    val researchJobs by viewModel.researchJobs.collectAsState()

    var selectedTab by remember { mutableStateOf("Directory") }
    var selectedAgentType by remember { mutableStateOf(ResearchAgentType.UK_UNIVERSITY_AGENT) }
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") }

    // Metrics Calculation
    val totalFound = researchedInstitutions.size
    val syncedCount = researchedInstitutions.count { it.isSyncedToMainCrm }
    val completedJobs = researchJobs.count { it.status == "Completed" }
    val totalDuplicatesPrevented = researchJobs.sumOf { it.duplicatesPrevented }
    val activeQueueCount = researchJobs.count { it.status == "Pending" || it.status == "In Progress" }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: CEO AI Research Intelligence Coordinator
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
                        color = Color(0xFFFF8A00),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.TravelExplore, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("UK UNIVERSITY RESEARCH ENGINE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFF8A00).copy(alpha = 0.25f)) {
                                Text("10 SPECIALIZED AGENTS", color = Color(0xFFFF8A00), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Continuous Discovery • Deduplication Engine • Public Sector Audit", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = { viewModel.runCeoResearchCampaign() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Run CEO Campaign", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // CEO DASHBOARD METRICS GRID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResearchMetricTile("New Found", "$totalFound", Color(0xFF00AEEF), Modifier.weight(1f))
                ResearchMetricTile("Synced CRM", "$syncedCount", Color(0xFF10B981), Modifier.weight(1f))
                ResearchMetricTile("Completed", "$completedJobs", Color(0xFF3B82F6), Modifier.weight(1f))
                ResearchMetricTile("Duplicates Blocked", "$totalDuplicatesPrevented", Color(0xFFFF8A00), Modifier.weight(1f))
                ResearchMetricTile("Queue", "$activeQueueCount", Color(0xFFFF2E93), Modifier.weight(1f))
            }

            // Quick Actions & Agent Dispatcher Bar
            Surface(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Dispatch Specialized Research Agent", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Agent Type Selector Dropdown / Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(ResearchAgentType.values()) { agent ->
                                FilterChip(
                                    selected = selectedAgentType == agent,
                                    onClick = { selectedAgentType = agent },
                                    label = { Text(agent.displayName, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFF8A00),
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                        labelColor = Color.White,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.dispatchSpecializedResearchAgent(selectedAgentType) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEEF)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispatch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedAgentType.description,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = { viewModel.syncResearchedInstitutionsToCrm() },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync All to Main CRM", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Sub-Navigation Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubTabButton("Discovered Institutions ($totalFound)", selectedTab == "Directory", Modifier.weight(1f)) { selectedTab = "Directory" }
                SubTabButton("Research Queue (${researchJobs.size})", selectedTab == "Queue", Modifier.weight(1f)) { selectedTab = "Queue" }
                SubTabButton("Daily CEO Digest", selectedTab == "Digest", Modifier.weight(1f)) { selectedTab = "Digest" }
            }

            // Content Section based on selectedTab
            when (selectedTab) {
                "Directory" -> DiscoveredDirectoryView(
                    institutions = researchedInstitutions,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    filterType = filterType,
                    onFilterChange = { filterType = it }
                )
                "Queue" -> ResearchQueueView(jobs = researchJobs)
                "Digest" -> CeoDailyDigestView(institutions = researchedInstitutions, jobs = researchJobs)
            }
        }
    }
}

@Composable
fun ResearchMetricTile(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color.Black.copy(alpha = 0.35f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
fun SubTabButton(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (selected) Color(0xFFFF8A00) else Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiscoveredDirectoryView(
    institutions: List<ResearchedInstitutionEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    filterType: String,
    onFilterChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search institution name, course, city, English requirements...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFFF8A00),
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
            )
        )

        // Phase 13 Multi-Tier Filter Chips Bar
        val filterOptions = listOf(
            "All Tiers",
            "Tier 1: Mid-ranked UK",
            "Tier 2: FE Colleges",
            "Tier 3: Private HE",
            "Tier 4: Pathway",
            "Tier 5: Foundation",
            "MOI Accepted",
            "Internal Test",
            "Scholarships"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { opt ->
                FilterChip(
                    selected = filterType == opt || (filterType == "All" && opt == "All Tiers"),
                    onClick = { onFilterChange(if (opt == "All Tiers") "All" else opt) },
                    label = { Text(opt, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF8A00),
                        containerColor = Color.White.copy(alpha = 0.1f),
                        labelColor = Color.White,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        val filtered = institutions.filter { inst ->
            val matchesQuery = searchQuery.isBlank() ||
                inst.institutionName.contains(searchQuery, ignoreCase = true) ||
                inst.city.contains(searchQuery, ignoreCase = true) ||
                inst.officialWebsite.contains(searchQuery, ignoreCase = true) ||
                inst.acceptedEnglishTests.contains(searchQuery, ignoreCase = true) ||
                inst.tierLevel.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (filterType) {
                "Tier 1: Mid-ranked UK" -> inst.tierLevel.contains("Tier 1", ignoreCase = true)
                "Tier 2: FE Colleges" -> inst.tierLevel.contains("Tier 2", ignoreCase = true) || inst.institutionType.contains("College", ignoreCase = true)
                "Tier 3: Private HE" -> inst.tierLevel.contains("Tier 3", ignoreCase = true) || inst.institutionType.contains("Private", ignoreCase = true)
                "Tier 4: Pathway" -> inst.tierLevel.contains("Tier 4", ignoreCase = true) || inst.institutionType.contains("Pathway", ignoreCase = true)
                "Tier 5: Foundation" -> inst.tierLevel.contains("Tier 5", ignoreCase = true) || inst.institutionType.contains("Foundation", ignoreCase = true)
                "MOI Accepted" -> inst.moiAccepted
                "Internal Test" -> inst.internalInterviewOffered
                "Scholarships" -> inst.scholarshipAvailable
                else -> true
            }

            matchesQuery && matchesFilter
        }.sortedByDescending { it.opportunityScore }

        if (filtered.isEmpty()) {
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FindInPage, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("No researched institutions found", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filtered.forEach { inst ->
                    ResearchedInstitutionCard(institution = inst)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResearchedInstitutionCard(institution: ResearchedInstitutionEntity) {
    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(institution.lastUpdated))

    Surface(
        color = Color.Black.copy(alpha = 0.35f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(institution.institutionName, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        if (institution.isSyncedToMainCrm) {
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF10B981).copy(alpha = 0.25f)) {
                                Text("CRM SYNCED", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Text("${institution.tierLevel} • ${institution.city}, ${institution.country}", color = Color(0xFF00AEEF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFF8A00).copy(alpha = 0.25f)) {
                    Text("Opportunity Score: ${institution.opportunityScore}/100", color = Color(0xFFFF8A00), fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BadgeChip("MOI Accepted: ${if (institution.moiAccepted) "YES 🎓" else "NO"}", if (institution.moiAccepted) Color(0xFF10B981) else Color(0xFFEF4444))
                BadgeChip("Internal Test: ${if (institution.internalInterviewOffered) "YES 🗣️" else "NO"}", Color(0xFF7C3AED))
                BadgeChip("Min IELTS: ${institution.minIeltsScore}", Color(0xFF00AEEF))
                BadgeChip("App Fee: ${institution.applicationFeeInfo}", Color(0xFFFF8A00))
                BadgeChip("Commission: ${institution.commissionInfo}", Color(0xFF3B82F6))
            }

            Text("🗣️ English Tests: ${institution.acceptedEnglishTests}", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text("📜 English Waiver Policy: ${institution.englishWaiverPolicy}", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
            Text("💷 CAS Deposit & Process: ${institution.depositRequirement} • ${institution.casProcessOverview}", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
            Text("⏱️ Processing Time: ${institution.averageProcessingTime}", color = Color(0xFFA78BFA), fontSize = 11.sp, fontWeight = FontWeight.Bold)

            if (institution.scholarshipDetails.isNotBlank()) {
                Text("🎓 Scholarships: ${institution.scholarshipDetails}", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }

            Text("📝 Entry & Policy: ${institution.entryRequirements}", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
            Text("🤝 Suitability & Partnership: ${institution.recruitmentSuitability} • ${institution.partnershipPotential}", color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Source: ${institution.researchSource}", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                Text("Audited: $formattedDate", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun BadgeChip(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(text, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun ResearchQueueView(jobs: List<ResearchJobEntity>) {
    if (jobs.isEmpty()) {
        Surface(
            color = Color.White.copy(alpha = 0.05f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No research jobs in queue", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            jobs.forEach { job ->
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(job.assignedAt))
                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(job.agentType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Surface(shape = RoundedCornerShape(4.dp), color = if (job.status == "Completed") Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFFF8A00).copy(alpha = 0.2f)) {
                                    Text(job.status, color = if (job.status == "Completed") Color(0xFF10B981) else Color(0xFFFF8A00), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text(job.details, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${job.resultsFound} found", color = Color(0xFF00AEEF), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("${job.duplicatesPrevented} dupes blocked", color = Color(0xFFFF8A00), fontSize = 9.sp)
                            Text(timeStr, color = Color.White.copy(alpha = 0.4f), fontSize = 8.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CeoDailyDigestView(
    institutions: List<ResearchedInstitutionEntity>,
    jobs: List<ResearchJobEntity>
) {
    val totalFound = institutions.size
    val agentRecruitCount = institutions.count { it.agentRecruitmentAvailable }
    val scholarshipCount = institutions.count { it.scholarshipAvailable }
    val moiCount = institutions.count { it.entryRequirements.contains("MOI", ignoreCase = true) }
    val freeAppCount = institutions.count { it.applicationFeeInfo.contains("Free", ignoreCase = true) }

    Surface(
        color = Color.Black.copy(alpha = 0.35f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFF8A00).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Summarize, contentDescription = null, tint = Color(0xFFFF8A00))
                Text("CEO AI DAILY RESEARCH EXECUTIVE DIGEST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            Text(
                "Executive Summary: NextBorder AIOS UK Research Intelligence Engine has completed continuous discovery across public UK university portals, international office directories, and scholarship databases. 10 specialized AI agents executed automated audits without visual redesign or external dependencies.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DigestBullet("Total Discovered & Audited Institutions", "$totalFound")
                DigestBullet("Agent Recruitment Available Institutions", "$agentRecruitCount")
                DigestBullet("International Merit Scholarships Available", "$scholarshipCount")
                DigestBullet("Medium of Instruction (MOI) Friendly Institutions", "$moiCount")
                DigestBullet("Direct Free / Waived Application Institutions", "$freeAppCount")
                DigestBullet("Automated Duplicates Blocked by Coordinator", "${jobs.sumOf { it.duplicatesPrevented }}")
            }

            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF10B981).copy(alpha = 0.15f)) {
                Text(
                    "✓ Deduplication engine active. Data stored cleanly in Room DB tables 'researched_institutions' and 'research_jobs'. Ready for Phase 10 auto-pitching execution.",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun DigestBullet(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("• $label", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        Text(value, color = Color(0xFFFF8A00), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
    }
}
