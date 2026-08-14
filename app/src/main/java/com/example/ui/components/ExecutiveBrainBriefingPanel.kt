package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.ChatMessage
import com.example.viewmodel.CrmViewModel

@Composable
fun ShimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color(0xFF7C3AED).copy(alpha = 0.15f),
            Color(0xFFA78BFA).copy(alpha = 0.45f),
            Color(0xFF7C3AED).copy(alpha = 0.15f)
        )
        val transition = rememberInfiniteTransition(label = "shimmerTransition")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerFloat"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - 300f, y = translateAnimation.value - 300f),
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        SolidColor(Color.Transparent)
    }
}

@Composable
fun ShimmerChatBubble() {
    val brush = ShimmerBrush()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = Color.White.copy(alpha = 0.08f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .widthIn(min = 220.dp, max = 290.dp)
                .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
fun ShimmerBriefingSkeleton() {
    val brush = ShimmerBrush()
    Surface(
        color = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
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
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(brush)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExecutiveBrainBriefingPanel(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val briefing by viewModel.founderBriefing.collectAsState()
    val isBriefingLoading by viewModel.isBriefingLoading.collectAsState()
    val isAiChatLoading by viewModel.isAiChatLoading.collectAsState()
    val currentLanguage by viewModel.briefingLanguage.collectAsState()
    val chatHistory by viewModel.executiveChatHistory.collectAsState()
    val geminiHealthState by viewModel.geminiHealthState.collectAsState()

    var userQueryText by remember { mutableStateOf("") }
    var isSpeaking by remember { mutableStateOf(false) }
    var showChatAssistant by remember { mutableStateOf(true) }

    // Performance Optimization: Wrap Gemini response chat history rendering in derivedStateOf
    val renderedChatHistory by remember {
        derivedStateOf {
            chatHistory.takeLast(6).map { chat ->
                val isUser = chat.sender.contains("Founder", ignoreCase = true)
                val formattedSender = if (isUser) "Founder Ishak Dawan" else "Executive AI Brain"
                chat.copy(sender = formattedSender, message = chat.message.trim())
            }
        }
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Panel Header & Language Switcher
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
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("FOUNDER EXECUTIVE AI BRAIN", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF7C3AED).copy(alpha = 0.25f)) {
                                Text("Phase 12", color = Color(0xFFA78BFA), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("CEO Strategic Assistant • Ishak Dawan", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                // Bilingual Toggle Switch (Bangla 🇧🇩 Default / English 🇬🇧)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentLanguage == "Bangla") Color(0xFF7C3AED) else Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {
                        TextButton(
                            onClick = { viewModel.setBriefingLanguage("Bangla") },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("🇧🇩 BN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentLanguage == "English") Color(0xFF7C3AED) else Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {
                        TextButton(
                            onClick = { viewModel.setBriefingLanguage("English") },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("🇬🇧 EN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            if (isBriefingLoading) {
                ShimmerBriefingSkeleton()
            } else briefing?.let { b ->
                // Founder Briefing Highlight Box
                Surface(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Briefing Top Bar: Greeting & Voice Playback
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(b.greeting, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                Text("${b.dateString} • ${b.currentBusinessStatus}", color = Color(0xFFA78BFA), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        if (isSpeaking) {
                                            viewModel.stopBriefingSpeech()
                                            isSpeaking = false
                                        } else {
                                            viewModel.speakBriefing(context)
                                            isSpeaking = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isSpeaking) Color(0xFFEF4444) else Color(0xFF7C3AED)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isSpeaking) "Stop" else "Voice Brief", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { viewModel.generateFounderBriefing() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate Briefing", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        val geminiStatusLabel = when (geminiHealthState.status) {
                            com.example.core.ai.AiHealthStatus.CONNECTED -> "Online (${geminiHealthState.lastLatencyMs}ms)"
                            com.example.core.ai.AiHealthStatus.TESTING -> "Testing..."
                            com.example.core.ai.AiHealthStatus.NO_KEY -> "No Key Set"
                            com.example.core.ai.AiHealthStatus.ERROR -> "Error"
                        }
                        val geminiStatusColor = when (geminiHealthState.status) {
                            com.example.core.ai.AiHealthStatus.CONNECTED -> Color(0xFF10B981)
                            com.example.core.ai.AiHealthStatus.TESTING -> Color(0xFFF59E0B)
                            com.example.core.ai.AiHealthStatus.NO_KEY -> Color(0xFFEF4444)
                            com.example.core.ai.AiHealthStatus.ERROR -> Color(0xFFEF4444)
                        }

                        // Briefing Data Grid (18 core Executive Metrics)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BriefingChip("Urgent Priorities", "${b.urgentPrioritiesCount}", Color(0xFFEF4444))
                            BriefingChip("Today's Meetings", "${b.meetingScheduleCount}", Color(0xFFEC4899))
                            BriefingChip("Reminders", "${b.todayRemindersCount}", Color(0xFFFF2E93))
                            BriefingChip("Pending Follow-ups", "${b.followupQueueCount}", Color(0xFFF59E0B))
                            BriefingChip("New Unis", "${b.newUniversitiesCount}", Color(0xFF10B981))
                            BriefingChip("New Colleges", "${b.newCollegesCount}", Color(0xFFFF8A00))
                            BriefingChip("Partnership Opportunities", "${b.partnershipOpportunitiesCount}", Color(0xFFA78BFA))
                            BriefingChip("Scholarships", "${b.scholarshipsCount}", Color(0xFF00AEEF))
                            BriefingChip("Email Queue", b.emailStatus, Color(0xFF3B82F6))
                            BriefingChip("Sheets Sync", b.googleSheetsSyncStatus, Color(0xFF10B981))
                            BriefingChip("Workers", b.backgroundWorkersStatus, Color(0xFFA78BFA))
                            BriefingChip("Gemini AI", geminiStatusLabel, geminiStatusColor)
                            BriefingChip("Room DB", b.databaseStatus, Color(0xFF10B981))
                            BriefingChip("Weekly Goal", b.weeklyGoalProgress, Color(0xFF00AEEF))
                        }

                        // Gemini Central Service Live Health Bar
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier.size(8.dp).clip(CircleShape).background(geminiStatusColor)
                                        )
                                        Text(
                                            "GEMINI SERVICE ENGINE (${geminiHealthState.activeModel})",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Text(
                                        "Requests: ${geminiHealthState.totalRequests} • Success: ${geminiHealthState.successCount} • Failures: ${geminiHealthState.failureCount}" +
                                                if (geminiHealthState.lastError != null) " • Error: ${geminiHealthState.lastError}" else "",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 10.sp
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.verifyGeminiConnectivity() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ping Test", color = Color(0xFFA78BFA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // AI Today's Strategic Recommendation
                        Surface(
                            color = Color(0xFF7C3AED).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(14.dp))
                                    Text("Today's AI Recommendation (আজকের এআই সুপারিশ)", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Text(b.aiRecommendation, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Productivity Mode Action Bar (Daily Plan / Daily Review / Weekly Review)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("PRODUCTIVITY MODE", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { viewModel.generateProductivityPlan("DailyPlan") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEEF)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("🌅 Daily Plan", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.generateProductivityPlan("DailyReview") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("🌙 Daily Review", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.generateProductivityPlan("WeeklyReview") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("📊 Weekly Review", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Interactive Conversational Executive Assistant
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("EXECUTIVE CHAT ASSISTANT", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    TextButton(
                        onClick = { showChatAssistant = !showChatAssistant },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (showChatAssistant) "Minimize" else "Expand Assistant", color = Color(0xFFA78BFA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (showChatAssistant) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Quick Founder Questions Row
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                QuickPromptChip("আজ কী গুরুত্বপূর্ণ?") { viewModel.sendExecutiveChatMessage("আজ কী গুরুত্বপূর্ণ?") }
                                QuickPromptChip("MOI গ্রহণকারী প্রতিষ্ঠান?") { viewModel.sendExecutiveChatMessage("MOI গ্রহণ করে এমন প্রতিষ্ঠান দেখাও।") }
                                QuickPromptChip("IELTS 6.0 গ্রহণযোগ্য?") { viewModel.sendExecutiveChatMessage("IELTS 6.0 গ্রহণ করে এমন প্রতিষ্ঠান দেখাও।") }
                                QuickPromptChip("Scholarship আছে?") { viewModel.sendExecutiveChatMessage("Scholarship আছে এমন প্রতিষ্ঠান দেখাও।") }
                                QuickPromptChip("আজকে Business Health কেমন?") { viewModel.sendExecutiveChatMessage("আজ আমার Business Health কেমন?") }
                                QuickPromptChip("আজ কতগুলো নতুন ইউনিভার্সিটি পেয়েছ?") { viewModel.sendExecutiveChatMessage("আজ কতগুলো নতুন ইউনিভার্সিটি পেয়েছ?") }
                                QuickPromptChip("আজ কাদের Follow-up করতে হবে?") { viewModel.sendExecutiveChatMessage("আজ কাদের Follow-up করতে হবে?") }
                                QuickPromptChip("আজকের Meeting কী?") { viewModel.sendExecutiveChatMessage("আজকের Meeting কী?") }
                            }

                            if (renderedChatHistory.isNotEmpty() || isAiChatLoading) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    renderedChatHistory.forEach { chat ->
                                        ExecutiveChatBubble(chat)
                                    }
                                    if (isAiChatLoading) {
                                        ShimmerChatBubble()
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = userQueryText,
                                    onValueChange = { userQueryText = it },
                                    placeholder = { Text("Ask CEO AI Assistant (বাংলা / English)...", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF7C3AED),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    singleLine = true
                                )

                                Button(
                                    onClick = {
                                        if (userQueryText.isNotBlank()) {
                                            viewModel.sendExecutiveChatMessage(userQueryText)
                                            userQueryText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
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
fun QuickPromptChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color(0xFF7C3AED).copy(alpha = 0.25f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
    ) {
        Text(text, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun BriefingChip(label: String, count: String, color: Color) {
    Surface(
        color = Color.Black.copy(alpha = 0.3f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
            Text(count, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
        }
    }
}

@Composable
fun ExecutiveChatBubble(chat: ChatMessage) {
    val isUser by remember(chat.sender) {
        derivedStateOf { chat.sender.contains("Founder", ignoreCase = true) }
    }
    val messageText by remember(chat.message) {
        derivedStateOf { chat.message }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) Color(0xFF7C3AED) else Color.White.copy(alpha = 0.12f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(chat.sender, color = if (isUser) Color.White.copy(alpha = 0.8f) else Color(0xFFA78BFA), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(messageText, color = Color.White, fontSize = 11.sp)
            }
        }
    }
}
