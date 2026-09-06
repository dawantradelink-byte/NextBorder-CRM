package com.example.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExecutiveMemoryEntity
import com.example.viewmodel.CrmViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExecutiveMemoryTimelinePanel(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val executiveMemories by viewModel.executiveMemories.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedTimeFilter by remember { mutableStateOf("All Time") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddMemoryDialog by remember { mutableStateOf(false) }

    // Quick Natural Queries (Bangla & English)
    val quickMemoryQueries = listOf(
        "Greenwich" to "University of Greenwich",
        "MOI Policy" to "MOI",
        "Partnerships" to "Partnership",
        "Scholarship" to "Bursary",
        "Teesside" to "Teesside",
        "Chester" to "Chester"
    )

    // Filter Logic
    val filteredMemories = executiveMemories.filter { mem ->
        val matchesQuery = searchQuery.isBlank() ||
            mem.title.contains(searchQuery, ignoreCase = true) ||
            mem.institutionName.contains(searchQuery, ignoreCase = true) ||
            mem.details.contains(searchQuery, ignoreCase = true) ||
            mem.meetingNotes.contains(searchQuery, ignoreCase = true)

        val matchesCategory = if (selectedCategory == "All") true else mem.category.equals(selectedCategory, ignoreCase = true)

        val now = System.currentTimeMillis()
        val dayMs = 86400000L
        val matchesTime = when (selectedTimeFilter) {
            "Today" -> (now - mem.timestamp) <= dayMs
            "Yesterday" -> (now - mem.timestamp) in dayMs..(dayMs * 2)
            "Last 7 Days" -> (now - mem.timestamp) <= (dayMs * 7)
            "Last 30 Days" -> (now - mem.timestamp) <= (dayMs * 30)
            else -> true
        }

        matchesQuery && matchesCategory && matchesTime
    }

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
                        color = Color(0xFFFF8A00),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("EXECUTIVE MEMORY & TIMELINE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFF8A00).copy(alpha = 0.25f)) {
                                Text("PHASE 16", color = Color(0xFFFF8A00), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Partnership History, Decisions & Founder Knowledge Log", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = { showAddMemoryDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Memory", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // CEO Quick Question Hints Box
            Surface(
                color = Color.Black.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFF8A00).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Quickreply, contentDescription = null, tint = Color(0xFFFF8A00), modifier = Modifier.size(16.dp))
                        Text("FOUNDER EXECUTIVE MEMORY SEARCH (Bangla & English)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickMemoryQueries.size) { idx ->
                            val (label, queryVal) = quickMemoryQueries[idx]
                            Surface(
                                color = Color(0xFFFF8A00).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { searchQuery = queryVal }
                            ) {
                                Text("🔍 $label", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }

            // Search Bar & Filters
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search meeting history, emails, MOI policies...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f)) },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = "Clear search query", tint = Color.White) } }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A00),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Timeline Filter Chips (Today, Yesterday, Last 7 Days, Last 30 Days, All)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val timeOptions = listOf("All Time", "Today", "Yesterday", "Last 7 Days", "Last 30 Days")
                    timeOptions.forEach { tOpt ->
                        FilterChip(
                            selected = selectedTimeFilter == tOpt,
                            onClick = { selectedTimeFilter = tOpt },
                            label = { Text(tOpt, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF8A00),
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Category Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val categories = listOf("All", "Partnership", "University Research", "Student Counselling", "Weekly Summary")
                    items(categories.size) { idx ->
                        val cat = categories[idx]
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00AEEF),
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Executive Memory Items List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("RECORDED EXECUTIVE MEMORIES (${filteredMemories.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                if (filteredMemories.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No executive memories found for this filter.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                } else {
                    filteredMemories.forEach { memory ->
                        ExecutiveMemoryCard(memory = memory, onDelete = { viewModel.deleteExecutiveMemory(memory) })
                    }
                }
            }
        }
    }

    if (showAddMemoryDialog) {
        AddMemoryDialog(
            onDismiss = { showAddMemoryDialog = false },
            onAddMemory = { newMem ->
                viewModel.addExecutiveMemory(newMem)
                showAddMemoryDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExecutiveMemoryCard(
    memory: ExecutiveMemoryEntity,
    onDelete: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.45f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Title & Priority Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFF8A00).copy(alpha = 0.2f)
                        ) {
                            Text(memory.category, color = Color(0xFFFF8A00), fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text(memory.title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                    if (memory.institutionName.isNotBlank()) {
                        Text("🏛️ Institution: ${memory.institutionName} • Status: ${memory.agentStatus}", color = Color(0xFF00AEEF), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981)
                    ) {
                        Text("Score: ${memory.priorityScore}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete memory", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Text(memory.details, color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)

            // Partnership Intelligence Badges
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MemoryBadge("First Contact: ${memory.firstContactDate}", Color(0xFFA78BFA))
                MemoryBadge("Last Contact: ${memory.lastContactDate}", Color(0xFF00AEEF))
                MemoryBadge("MOI Policy: ${memory.moiPolicy}", Color(0xFF10B981))
                MemoryBadge("English Req: ${memory.englishReq}", Color(0xFFFF8A00))
                MemoryBadge("Interview: ${memory.interviewPolicy}", Color(0xFFEC4899))
                MemoryBadge("Risk Score: ${memory.riskScore}%", if (memory.riskScore < 20) Color(0xFF10B981) else Color(0xFFEF4444))
            }

            if (memory.meetingNotes.isNotBlank()) {
                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Notes, contentDescription = null, tint = Color(0xFF00AEEF), modifier = Modifier.size(14.dp))
                        Text("Meeting Notes: ${memory.meetingNotes}", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun AddMemoryDialog(
    onDismiss: () -> Unit,
    onAddMemory: (ExecutiveMemoryEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Partnership") }
    var institutionName by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var meetingNotes by remember { mutableStateOf("") }
    var moiPolicy by remember { mutableStateOf("MOI Accepted within 5 years") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Executive Memory & Notes", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Memory Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Partnership, Meeting, Research)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = institutionName,
                    onValueChange = { institutionName = it },
                    label = { Text("Institution Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Executive Details") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = meetingNotes,
                    onValueChange = { meetingNotes = it },
                    label = { Text("Meeting / Communication Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newMemory = ExecutiveMemoryEntity(
                            title = title,
                            category = category,
                            institutionName = institutionName,
                            details = details,
                            meetingNotes = meetingNotes,
                            moiPolicy = moiPolicy
                        )
                        onAddMemory(newMemory)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00))
            ) {
                Text("Save Memory")
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
