package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedAgentAvatar(
    agentType: String, // "Partnerships", "Strategy", "HR", "Security"
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    isActive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "agent_anim_$agentType")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura"
    )

    val (primaryColor, secondaryColor, icon) = when (agentType) {
        "Partnerships" -> Triple(Color(0xFF3B82F6), Color(0xFF60A5FA), Icons.Default.Handshake)
        "Strategy" -> Triple(Color(0xFFF59E0B), Color(0xFFFBBF24), Icons.Default.Psychology)
        "HR" -> Triple(Color(0xFF10B981), Color(0xFF34D399), Icons.Default.Groups)
        "Security" -> Triple(Color(0xFF8B5CF6), Color(0xFFA78BFA), Icons.Default.Security)
        else -> Triple(Color(0xFFFF2E93), Color(0xFFFF75C3), Icons.Default.SmartToy)
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing glowing aura
        if (isActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(primaryColor.copy(alpha = auraAlpha), Color.Transparent)
                        )
                    )
            )
        }

        // Canvas Orbit Radar Arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 2.dp.toPx()
            drawCircle(
                color = primaryColor.copy(alpha = 0.3f),
                style = Stroke(width = strokeWidth)
            )
        }

        // Rotating Accent Orbit Ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryColor,
                            secondaryColor,
                            Color.Transparent,
                            primaryColor
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Inner Core Badge
        Box(
            modifier = Modifier
                .size(size * 0.72f)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.85f),
                            secondaryColor.copy(alpha = 0.4f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * 0.38f)
            )
        }

        // Active Status Indicator Dot
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(size * 0.24f)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                    .border(1.5.dp, Color.Black, CircleShape)
            )
        }
    }
}
