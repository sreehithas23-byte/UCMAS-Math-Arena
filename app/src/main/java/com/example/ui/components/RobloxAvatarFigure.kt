package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RobloxAvatarFigure(
    shirtName: String = "Academy Red Blazer",
    pantsName: String = "Classic Black Slacks",
    accessoryName: String = "Golden Abacus Belt",
    headName: String = "Classic Blocky Head",
    scale: Float = 1.0f,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isJumping by remember { mutableStateOf(false) }
    val jumpOffsetY by animateFloatAsState(
        targetValue = if (isJumping) -20f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        finishedListener = { isJumping = false },
        label = "jump"
    )

    val shirtColor = when (shirtName) {
        "Cyber Neon Hoodie" -> Color(0xFF00E5FF)
        "Golden Dragon Gi" -> Color(0xFFFFB300)
        "Teacher Grandmaster Robe" -> Color(0xFF6A1B9A)
        else -> Color(0xFFD32F2F) // Academy Red Blazer / Red Uniform
    }

    val pantsColor = when (pantsName) {
        "Speedster Track Pants" -> Color(0xFF1565C0)
        "Imperial Gold Armor Pants" -> Color(0xFFFF8F00)
        "National Championship Hakama" -> Color(0xFFB71C1C)
        else -> Color(0xFF212121) // Classic Black Slacks
    }

    val hasCrown = accessoryName.contains("Crown") || accessoryName.contains("Laurel")
    val hasVisor = accessoryName.contains("Visor")
    val hasAura = accessoryName.contains("Aura")
    val hasBelt = accessoryName.contains("Belt")

    Box(
        modifier = modifier
            .size((140 * scale).dp, (170 * scale).dp)
            .clickable {
                isJumping = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val topY = 24.dp.toPx() + jumpOffsetY

            // Dragon Speed Aura glow
            if (hasAura) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF5252).copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(cx, h / 2 + jumpOffsetY),
                        radius = w * 0.65f
                    ),
                    radius = w * 0.65f,
                    center = Offset(cx, h / 2 + jumpOffsetY)
                )
            }

            // Head (Blocky Roblox head)
            val headW = 44.dp.toPx() * scale
            val headH = 38.dp.toPx() * scale
            val headLeft = cx - headW / 2
            val skinTone = Color(0xFFFFE082)

            drawRoundRect(
                color = skinTone,
                topLeft = Offset(headLeft, topY),
                size = Size(headW, headH),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Roblox Face: Two oval black eyes & smile
            val eyeRadius = 3.dp.toPx() * scale
            drawCircle(Color.Black, eyeRadius, Offset(cx - 10.dp.toPx() * scale, topY + 16.dp.toPx() * scale))
            drawCircle(Color.Black, eyeRadius, Offset(cx + 10.dp.toPx() * scale, topY + 16.dp.toPx() * scale))

            if (!hasVisor) {
                // Cheerful Roblox Smile
                drawArc(
                    color = Color.Black,
                    startAngle = 10f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(cx - 9.dp.toPx() * scale, topY + 18.dp.toPx() * scale),
                    size = Size(18.dp.toPx() * scale, 10.dp.toPx() * scale),
                    style = Stroke(width = 2.dp.toPx() * scale)
                )
            } else {
                // Futuristic Cyber Visor
                drawRoundRect(
                    color = Color(0xFF00E5FF).copy(alpha = 0.85f),
                    topLeft = Offset(cx - 18.dp.toPx() * scale, topY + 11.dp.toPx() * scale),
                    size = Size(36.dp.toPx() * scale, 11.dp.toPx() * scale),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
            }

            // Crown Accessory
            if (hasCrown) {
                drawRoundRect(
                    color = Color(0xFFFFD700),
                    topLeft = Offset(headLeft - 2.dp.toPx(), topY - 10.dp.toPx() * scale),
                    size = Size(headW + 4.dp.toPx(), 12.dp.toPx() * scale),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
                // Crown spikes
                drawCircle(Color(0xFFFF1744), 3.dp.toPx(), Offset(cx, topY - 10.dp.toPx() * scale))
            }

            // Torso (Blocky Shirt)
            val torsoTop = topY + headH + 2.dp.toPx()
            val torsoW = 50.dp.toPx() * scale
            val torsoH = 46.dp.toPx() * scale
            val torsoLeft = cx - torsoW / 2

            drawRoundRect(
                color = shirtColor,
                topLeft = Offset(torsoLeft, torsoTop),
                size = Size(torsoW, torsoH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Torso tie/logo detail
            drawCircle(Color.White.copy(alpha = 0.4f), 5.dp.toPx() * scale, Offset(cx, torsoTop + 14.dp.toPx() * scale))

            // Left Arm
            val armW = 16.dp.toPx() * scale
            val armH = 42.dp.toPx() * scale
            drawRoundRect(
                color = shirtColor.copy(alpha = 0.9f),
                topLeft = Offset(torsoLeft - armW - 2.dp.toPx(), torsoTop),
                size = Size(armW, armH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Left Hand
            drawRoundRect(
                color = skinTone,
                topLeft = Offset(torsoLeft - armW - 2.dp.toPx(), torsoTop + armH),
                size = Size(armW, 10.dp.toPx() * scale),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Right Arm
            drawRoundRect(
                color = shirtColor.copy(alpha = 0.9f),
                topLeft = Offset(torsoLeft + torsoW + 2.dp.toPx(), torsoTop),
                size = Size(armW, armH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Right Hand
            drawRoundRect(
                color = skinTone,
                topLeft = Offset(torsoLeft + torsoW + 2.dp.toPx(), torsoTop + armH),
                size = Size(armW, 10.dp.toPx() * scale),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Abacus Belt Accessory
            if (hasBelt) {
                drawRect(
                    color = Color(0xFFFFD700),
                    topLeft = Offset(torsoLeft, torsoTop + torsoH - 8.dp.toPx() * scale),
                    size = Size(torsoW, 8.dp.toPx() * scale)
                )
                drawCircle(Color(0xFF8D6E63), 3.dp.toPx(), Offset(cx, torsoTop + torsoH - 4.dp.toPx() * scale))
            }

            // Legs / Pants (Dual blocky legs)
            val legTop = torsoTop + torsoH
            val legW = 23.dp.toPx() * scale
            val legH = 42.dp.toPx() * scale

            // Left Leg
            drawRoundRect(
                color = pantsColor,
                topLeft = Offset(cx - legW - 2.dp.toPx(), legTop),
                size = Size(legW, legH),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Right Leg
            drawRoundRect(
                color = pantsColor,
                topLeft = Offset(cx + 2.dp.toPx(), legTop),
                size = Size(legW, legH),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
        }
    }
}
