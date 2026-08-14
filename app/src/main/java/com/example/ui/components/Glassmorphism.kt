package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AiosAccent
import com.example.ui.theme.AiosGlassBorder
import com.example.ui.theme.AiosPrimary
import com.example.ui.theme.AiosSecondary
import com.example.ui.theme.AiosSurface

@Composable
fun GlassBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AiosSurface,
                        AiosSecondary,
                        AiosSurface
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Cyan & Accent Glowing Radial Blobs
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AiosPrimary.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(width * 0.15f, height * 0.1f),
                    radius = width * 0.5f
                ),
                radius = width * 0.5f,
                center = Offset(width * 0.15f, height * 0.1f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AiosAccent.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(width * 0.85f, height * 0.35f),
                    radius = width * 0.45f
                ),
                radius = width * 0.45f,
                center = Offset(width * 0.85f, height * 0.35f)
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AiosPrimary.copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(width * 0.75f, height * 0.85f),
                    radius = width * 0.5f
                ),
                radius = width * 0.5f,
                center = Offset(width * 0.75f, height * 0.85f)
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AiosAccent.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(width * 0.2f, height * 0.9f),
                    radius = width * 0.4f
                ),
                radius = width * 0.4f,
                center = Offset(width * 0.2f, height * 0.9f)
            )
        }
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0x1FFFFFFF),
    contentColor: Color = Color.White,
    borderColor: Color = AiosGlassBorder,
    cornerRadius: Int = 20,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius.dp)),
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = 0.dp
    ) {
        content()
    }
}

