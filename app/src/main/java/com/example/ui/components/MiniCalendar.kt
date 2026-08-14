package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.University
import java.util.Calendar

@Composable
fun MiniCalendar(universities: List<University>, modifier: Modifier = Modifier) {
    val pendingFollowUps = universities.count { it.status.startsWith("FollowUp") }
    val upcomingMeetings = universities.count { it.status == "Meeting" }

    val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_MONTH)
    val monthNames = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val monthName = monthNames[calendar.get(Calendar.MONTH)]
    val year = calendar.get(Calendar.YEAR)

    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val highlightedFollowUps = mutableSetOf<Int>()
    if (pendingFollowUps > 0) {
        highlightedFollowUps.add((today + 1).coerceAtMost(daysInMonth))
        highlightedFollowUps.add((today + 2).coerceAtMost(daysInMonth))
    }

    val highlightedMeetings = mutableSetOf<Int>()
    if (upcomingMeetings > 0) {
        highlightedMeetings.add((today + 3).coerceAtMost(daysInMonth))
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
                    text = "$monthName $year",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IndicatorDot(color = Color(0xFFFF8A00), label = "Follow-up")
                    IndicatorDot(color = Color(0xFFFF2E93), label = "Meeting")
                }
            }

            val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            val weeks = (daysInMonth + firstDayOfWeek + 6) / 7
            for (week in 0 until weeks) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (dayOfWeek in 0 until 7) {
                        val dayOfMonth = week * 7 + dayOfWeek - firstDayOfWeek + 1
                        if (dayOfMonth in 1..daysInMonth) {
                            DayCell(
                                day = dayOfMonth,
                                isToday = dayOfMonth == today,
                                isFollowUp = highlightedFollowUps.contains(dayOfMonth),
                                isMeeting = highlightedMeetings.contains(dayOfMonth),
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    isToday: Boolean,
    isFollowUp: Boolean,
    isMeeting: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        val backgroundColor = when {
            isMeeting -> Color(0xFFFF2E93).copy(alpha = 0.2f)
            isFollowUp -> Color(0xFFFF8A00).copy(alpha = 0.2f)
            isToday -> Color(0xFF0D0B21).copy(alpha = 0.5f)
            else -> Color.Transparent
        }

        val textColor = when {
            isMeeting -> Color(0xFFFF2E93)
            isFollowUp -> Color(0xFFFF8A00)
            isToday -> Color(0xFF0D0B21)
            else -> Color.White
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                fontWeight = if (isToday || isFollowUp || isMeeting) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
            
            if (isMeeting || isFollowUp) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isMeeting) Color(0xFFFF2E93) else Color(0xFFFF8A00))
                )
            }
        }
    }
}

@Composable
fun IndicatorDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
    }
}
