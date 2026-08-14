package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReminderEntity
import com.example.viewmodel.CrmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalProductivityPanel(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reminders by viewModel.reminders.collectAsState()
    val todos by viewModel.todos.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilterCategory by remember { mutableStateOf("All") }

    val now = System.currentTimeMillis()
    val totalReminders = reminders.size
    val pendingReminders = reminders.filter { !it.isCompleted }
    val completedReminders = reminders.filter { it.isCompleted }
    val overdueReminders = pendingReminders.filter { it.triggerTimeMs < now }
    val todayReminders = pendingReminders.filter {
        val cal1 = Calendar.getInstance().apply { timeInMillis = it.triggerTimeMs }
        val cal2 = Calendar.getInstance()
        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Personal Assistant & Background Manager Status
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
                        color = Color(0xFF00AEEF),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("PERSONAL AI ASSISTANT", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF10B981).copy(alpha = 0.2f)) {
                                Text("WORKMANAGER ACTIVE", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Single-Owner Productivity Mode • Background Research & Smart Sync", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = { viewModel.triggerBackgroundSyncNow(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEEF)),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // Dashboard Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricMiniPill("Today's Tasks", "${todos.count { !it.isCompleted }}", Color(0xFF00AEEF), Modifier.weight(1f))
                MetricMiniPill("Pending", "${pendingReminders.size}", Color(0xFFFF8A00), Modifier.weight(1f))
                MetricMiniPill("Overdue", "${overdueReminders.size}", if (overdueReminders.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF10B981), Modifier.weight(1f))
                MetricMiniPill("Completed", "${completedReminders.size}", Color(0xFF10B981), Modifier.weight(1f))
            }

            // Quick Create Reminder Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personal Reminders (${pendingReminders.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E93)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddAlarm, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Reminder", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Category Filter Chips Row
            val categories = listOf("All", "Business", "Meeting", "Personal", "Visa", "University", "Payment", "Custom")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedFilterCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00AEEF),
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Reminders List
            val filteredReminders = reminders.filter {
                if (selectedFilterCategory == "All") true else it.category.equals(selectedFilterCategory, ignoreCase = true)
            }

            if (filteredReminders.isEmpty()) {
                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("No active reminders in '$selectedFilterCategory'", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredReminders.take(6).forEach { reminder ->
                        ReminderRowItem(
                            reminder = reminder,
                            onToggleComplete = { viewModel.toggleReminderCompleted(reminder) },
                            onSnooze = { viewModel.snoozeReminder(reminder, 15) },
                            onDelete = { viewModel.deleteReminder(reminder) }
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet for Unlimited Personal Reminder Creation
    if (showAddDialog) {
        CreateReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, priority, recurrence, customDate, customTime, triggerTimeMs, repeatInterval, notes ->
                viewModel.addPersonalReminder(
                    title = title,
                    category = category,
                    priority = priority,
                    recurrence = recurrence,
                    customDate = customDate,
                    customTime = customTime,
                    triggerTimeMs = triggerTimeMs,
                    repeatIntervalMinutes = repeatInterval,
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun MetricMiniPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color.Black.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, maxLines = 1)
        }
    }
}

@Composable
fun ReminderRowItem(
    reminder: ReminderEntity,
    onToggleComplete: () -> Unit,
    onSnooze: () -> Unit,
    onDelete: () -> Unit
) {
    val isOverdue = !reminder.isCompleted && reminder.triggerTimeMs < System.currentTimeMillis()
    val formattedTime = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(reminder.triggerTimeMs))

    Surface(
        color = when {
            reminder.isCompleted -> Color.Black.copy(alpha = 0.2f)
            isOverdue -> Color(0xFFEF4444).copy(alpha = 0.15f)
            else -> Color.White.copy(alpha = 0.08f)
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(
            1.dp,
            if (isOverdue) Color(0xFFEF4444).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
            RoundedCornerShape(12.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    checked = reminder.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF10B981),
                        uncheckedColor = Color.White.copy(alpha = 0.6f)
                    )
                )

                Column {
                    Text(
                        text = reminder.title,
                        color = if (reminder.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (reminder.category) {
                                "Visa" -> Color(0xFFFF2E93)
                                "University" -> Color(0xFF3B82F6)
                                "Payment" -> Color(0xFF10B981)
                                "Meeting" -> Color(0xFFFF8A00)
                                else -> Color(0xFF00AEEF)
                            }.copy(alpha = 0.2f)
                        ) {
                            Text(
                                reminder.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = formattedTime,
                            fontSize = 10.sp,
                            color = if (isOverdue) Color(0xFFEF4444) else Color.White.copy(alpha = 0.6f),
                            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                        )

                        if (reminder.snoozeCount > 0) {
                            Text("• Snoozed ${reminder.snoozeCount}x", fontSize = 10.sp, color = Color(0xFFFF8A00))
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                if (!reminder.isCompleted) {
                    IconButton(onClick = onSnooze, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Snooze, contentDescription = "Snooze 15m", tint = Color(0xFFFF8A00), modifier = Modifier.size(16.dp))
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, priority: String, recurrence: String, customDate: String, customTime: String, triggerTimeMs: Long, repeatIntervalMinutes: Int, notes: String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Business") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var selectedRecurrence by remember { mutableStateOf("One-time") }
    var repeatInterval by remember { mutableIntStateOf(15) }
    var notes by remember { mutableStateOf("") }

    val calendar = remember { Calendar.getInstance() }
    var selectedDateStr by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)) }
    var selectedTimeStr by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2638),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AlarmAdd, contentDescription = null, tint = Color(0xFF00AEEF))
                Text("Create Personal Reminder", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Title *", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00AEEF),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                // Category selection
                Text("Category:", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val categories = listOf("Business", "Meeting", "Personal", "Visa", "University", "Payment", "Custom")
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00AEEF),
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Priority & Recurrence selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Priority:", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        val priorities = listOf("Low", "Medium", "High", "Urgent")
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            priorities.forEach { p ->
                                FilterChip(
                                    selected = selectedPriority == p,
                                    onClick = { selectedPriority = p },
                                    label = { Text(p, fontSize = 9.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (p == "Urgent") Color(0xFFEF4444) else Color(0xFFFF8A00),
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Date & Time pickers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH)
                            val day = calendar.get(Calendar.DAY_OF_MONTH)
                            DatePickerDialog(context, { _, y, m, d ->
                                calendar.set(y, m, d)
                                selectedDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                            }, year, month, day).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedDateStr, fontSize = 11.sp, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            val hour = calendar.get(Calendar.HOUR_OF_DAY)
                            val minute = calendar.get(Calendar.MINUTE)
                            TimePickerDialog(context, { _, h, m ->
                                calendar.set(Calendar.HOUR_OF_DAY, h)
                                calendar.set(Calendar.MINUTE, m)
                                selectedTimeStr = String.format("%02d:%02d", h, m)
                            }, hour, minute, true).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedTimeStr, fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title,
                            selectedCategory,
                            selectedPriority,
                            selectedRecurrence,
                            selectedDateStr,
                            selectedTimeStr,
                            calendar.timeInMillis,
                            repeatInterval,
                            notes
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEEF))
            ) {
                Text("Create Reminder", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}
