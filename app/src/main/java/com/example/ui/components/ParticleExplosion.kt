package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class Particle(
    val startX: Float,
    val startY: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float,
    val isCoin: Boolean
)

@Composable
fun ParticleExplosion(
    isActive: Boolean,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFFC107), // Amber
            Color(0xFFFF5252), // Crimson Spark
            Color(0xFFFFF9C4), // Light Gold
            Color(0xFFFF9800)  // Orange
        )
        List(65) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 900f + 300f
            Particle(
                startX = 0.5f,
                startY = 0.45f,
                vx = kotlin.math.cos(angle) * speed,
                vy = kotlin.math.sin(angle) * speed - 250f, // upward burst
                color = colors[Random.nextInt(colors.size)],
                size = Random.nextFloat() * 14f + 8f,
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                isCoin = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
        )
        onAnimationEnd()
    }

    val t = progress.value
    val alpha = (1f - t * t).coerceIn(0f, 1f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        for (p in particles) {
            val currentX = p.startX * w + p.vx * t
            val currentY = p.startY * h + p.vy * t + 0.5f * 980f * t * t // Gravity
            val currentRot = p.rotationSpeed * t

            rotate(currentRot, pivot = Offset(currentX, currentY)) {
                if (p.isCoin) {
                    // Gold Coin Outer
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.size,
                        center = Offset(currentX, currentY)
                    )
                    // Inner Rim
                    drawCircle(
                        color = Color(0xFFFFF9C4).copy(alpha = alpha * 0.9f),
                        radius = p.size * 0.65f,
                        center = Offset(currentX, currentY)
                    )
                } else {
                    // Star / Sparkle Diamond
                    val half = p.size
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(currentX - half / 2, currentY - half / 2),
                        size = androidx.compose.ui.geometry.Size(half, half)
                    )
                }
            }
        }
    }
}
