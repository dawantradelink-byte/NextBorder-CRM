package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AiosGlassBorder
import com.example.ui.theme.AiosPrimary
import com.example.ui.theme.AiosSecondary

@Composable
fun NextBorderLogo(modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = Color(0x241D2433),
        borderColor = AiosGlassBorder,
        cornerRadius = 24
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Vector Canvas Brand Icon (Shield + Globe + Forward Arrow + AI Circuit)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AiosSecondary)
                        .border(1.dp, AiosPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(38.dp)) {
                        val w = size.width
                        val h = size.height

                        // 1. Trust Shield Outline
                        val shieldPath = Path().apply {
                            moveTo(w * 0.5f, h * 0.05f)
                            cubicTo(w * 0.75f, h * 0.05f, w * 0.95f, h * 0.15f, w * 0.95f, h * 0.35f)
                            cubicTo(w * 0.95f, h * 0.72f, w * 0.5f, h * 0.98f, w * 0.5f, h * 0.98f)
                            cubicTo(w * 0.5f, h * 0.98f, w * 0.05f, h * 0.72f, w * 0.05f, h * 0.35f)
                            cubicTo(w * 0.05f, h * 0.15f, w * 0.25f, h * 0.05f, w * 0.5f, h * 0.05f)
                            close()
                        }
                        drawPath(path = shieldPath, color = Color(0xFF00AEEF), style = Stroke(width = 3.5f))

                        // 2. Globe Meridians & Latitudes (Silver Accent #C0C0C0)
                        val silverColor = Color(0xFFC0C0C0)
                        val lineStroke = Stroke(width = 2f)

                        // Latitudes
                        drawPath(Path().apply { moveTo(w * 0.22f, h * 0.35f); cubicTo(w * 0.35f, h * 0.28f, w * 0.65f, h * 0.28f, w * 0.78f, h * 0.35f) }, color = silverColor, style = lineStroke)
                        drawPath(Path().apply { moveTo(w * 0.15f, h * 0.50f); cubicTo(w * 0.32f, h * 0.44f, w * 0.68f, h * 0.44f, w * 0.85f, h * 0.50f) }, color = silverColor, style = lineStroke)
                        drawPath(Path().apply { moveTo(w * 0.24f, h * 0.65f); cubicTo(w * 0.36f, h * 0.60f, w * 0.64f, h * 0.60f, w * 0.76f, h * 0.65f) }, color = silverColor, style = lineStroke)

                        // Longitudes
                        drawPath(Path().apply { moveTo(w * 0.50f, h * 0.10f); cubicTo(w * 0.28f, h * 0.30f, w * 0.28f, h * 0.70f, w * 0.50f, h * 0.90f) }, color = silverColor, style = lineStroke)
                        drawPath(Path().apply { moveTo(w * 0.50f, h * 0.10f); cubicTo(w * 0.72f, h * 0.30f, w * 0.72f, h * 0.70f, w * 0.50f, h * 0.90f) }, color = silverColor, style = lineStroke)

                        // 3. Forward Arrow (Immigration / Global Movement)
                        val arrowPath = Path().apply {
                            moveTo(w * 0.32f, h * 0.62f)
                            lineTo(w * 0.32f, h * 0.44f)
                            lineTo(w * 0.55f, h * 0.44f)
                            lineTo(w * 0.55f, h * 0.34f)
                            lineTo(w * 0.78f, h * 0.50f)
                            lineTo(w * 0.55f, h * 0.66f)
                            lineTo(w * 0.55f, h * 0.56f)
                            lineTo(w * 0.32f, h * 0.56f)
                            close()
                        }
                        drawPath(path = arrowPath, color = Color.White)

                        // 4. AI Circuit Dots
                        drawCircle(color = Color(0xFF00AEEF), radius = 3.5f, center = Offset(w * 0.20f, h * 0.20f))
                        drawCircle(color = Color(0xFF00AEEF), radius = 3.5f, center = Offset(w * 0.80f, h * 0.20f))
                    }
                }

                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                append("NextBorder ")
                            }
                            withStyle(style = SpanStyle(color = AiosPrimary, fontWeight = FontWeight.ExtraBold)) {
                                append("AIOS")
                            }
                        },
                        fontSize = 20.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFC0C0C0),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Global Education • Immigration • AI",
                            color = Color(0xFFC0C0C0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
