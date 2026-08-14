package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.TodoItem

@Composable
fun TodoListSection(
    todos: List<TodoItem>,
    onAddTodo: (String, Long?, String) -> Unit,
    onToggleTodo: (TodoItem) -> Unit,
    onDeleteTodo: (TodoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var newTaskText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var activeFilter by remember { mutableStateOf("Pending") } // All, Pending, Overdue, Completed
    var todoToDelete by remember { mutableStateOf<TodoItem?>(null) }

    val filteredTodos = remember(todos, activeFilter) {
        val now = System.currentTimeMillis()
        when (activeFilter) {
            "Pending" -> todos.filter { !it.isCompleted }
            "Overdue" -> todos.filter { !it.isCompleted && it.dueDate != null && it.dueDate < now }
            "Completed" -> todos.filter { it.isCompleted }
            else -> todos
        }
    }

    if (todoToDelete != null) {
        AlertDialog(
            onDismissRequest = { todoToDelete = null },
            title = { Text("Delete Task?") },
            text = { Text("Are you sure you want to delete '${todoToDelete?.task}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        todoToDelete?.let { onDeleteTodo(it) }
                        todoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { todoToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    GlassCard(
        modifier = modifier.fillMaxWidth()
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
                Text(
                    "Follow-Ups & Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "${todos.count { !it.isCompleted }} Pending",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    placeholder = { Text("Add follow-up task...", color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier.weight(1f),
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

                // Priority dropdown
                FilterChip(
                    selected = selectedPriority == "High",
                    onClick = {
                        selectedPriority = when (selectedPriority) {
                            "Medium" -> "High"
                            "High" -> "Low"
                            else -> "Medium"
                        }
                    },
                    label = { Text(selectedPriority) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF2E93),
                        containerColor = Color.White.copy(alpha = 0.1f),
                        labelColor = Color.White,
                        selectedLabelColor = Color.White
                    )
                )

                IconButton(
                    onClick = {
                        if (newTaskText.isNotBlank()) {
                            onAddTodo(newTaskText, null, selectedPriority)
                            newTaskText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint = Color(0xFFFF8A00)
                    )
                }
            }

            // Filter Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Pending", "Overdue", "Completed", "All").forEach { filter ->
                    FilterChip(
                        selected = activeFilter == filter,
                        onClick = { activeFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF8A00),
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredTodos.isEmpty()) {
                Text(
                    "No $activeFilter tasks found.",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    filteredTodos.forEach { todo ->
                        val isOverdue = todo.dueDate != null && todo.dueDate < System.currentTimeMillis() && !todo.isCompleted

                        Surface(
                            color = if (isOverdue) Color(0x33EF4444) else Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onToggleTodo(todo) }
                                ) {
                                    Icon(
                                        imageVector = if (todo.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                        contentDescription = if (todo.isCompleted) "Completed" else "Pending",
                                        tint = if (todo.isCompleted) Color(0xFFFF8A00) else Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = todo.task,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            ),
                                            color = if (todo.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White
                                        )

                                        if (isOverdue) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "Overdue Task",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFFEF4444)
                                                )
                                            }
                                        }
                                    }
                                }

                                Surface(
                                    color = when (todo.priority) {
                                        "High" -> Color(0xFFEF4444)
                                        "Medium" -> Color(0xFFFF8A00)
                                        else -> Color(0xFF10B981)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        todo.priority,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { todoToDelete = todo },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Task",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
