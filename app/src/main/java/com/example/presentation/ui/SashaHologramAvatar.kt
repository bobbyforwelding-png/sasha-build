package com.example.presentation.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import kotlin.math.*
import kotlin.random.Random

enum class AvatarState {
    IDLE,
    THINKING,
    SPEAKING
}

@Composable
fun SashaHologramAvatar(
    state: AvatarState = AvatarState.IDLE,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF00E5FF),
    accentColor: Color = Color(0xFFFF4D4D),
    glowColor: Color = Color(0xFF00E5FF)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sasha_avatar")

    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "idle_pulse"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring_rotation"
    )

    val ringRotation2 by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring_rotation2"
    )

    val thinkingWave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Restart),
        label = "thinking_wave"
    )

    val speakingPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(150, easing = LinearEasing), RepeatMode.Reverse),
        label = "speaking_pulse"
    )

    val particles = remember {
        List(40) {
            ParticleData(
                angle = Random.nextFloat() * 360f,
                radius = Random.nextFloat() * 0.4f + 0.1f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                size = Random.nextFloat() * 3f + 1f,
                alpha = Random.nextFloat() * 0.6f + 0.2f
            )
        }
    }

    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "particle_phase"
    )

    val waveBars = remember { List(24) { Random.nextFloat() } }
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            when (state) {
                AvatarState.THINKING -> tween(600, easing = LinearEasing)
                AvatarState.SPEAKING -> tween(200, easing = LinearEasing)
                AvatarState.IDLE -> tween(3000, easing = LinearEasing)
            }, RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val breathAlpha = when (state) {
        AvatarState.THINKING -> 0.7f
        AvatarState.SPEAKING -> speakingPulse
        AvatarState.IDLE -> idlePulse
    }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val baseRadius = minOf(cx, cy) * 0.35f

        // Outer glow halo
        for (i in 5 downTo 0) {
            val haloRadius = baseRadius * (1.4f + i * 0.12f)
            drawCircle(
                color = glowColor.copy(alpha = 0.03f * breathAlpha),
                radius = haloRadius,
                center = Offset(cx, cy)
            )
        }

        // Orbital rings
        drawContext.canvas.save()
        drawContext.canvas.rotate(ringRotation, cx, cy)
        drawOvalRing(cx, cy, baseRadius * 1.15f, baseRadius * 0.35f, primaryColor.copy(alpha = 0.25f * breathAlpha), 1.5f)
        drawContext.canvas.restore()

        drawContext.canvas.save()
        drawContext.canvas.rotate(ringRotation2, cx, cy)
        drawOvalRing(cx, cy, baseRadius * 1.05f, baseRadius * 0.25f, accentColor.copy(alpha = 0.15f * breathAlpha), 1f)
        drawContext.canvas.restore()

        // Vertical ring
        drawContext.canvas.save()
        drawContext.canvas.rotate(60f, cx, cy)
        drawContext.canvas.rotate(ringRotation * 0.7f, cx, cy)
        drawOvalRingV(cx, cy, baseRadius * 1.0f, primaryColor.copy(alpha = 0.18f * breathAlpha), 1f)
        drawContext.canvas.restore()

        // Female silhouette
        drawFemaleSilhouette(cx, cy, baseRadius, primaryColor.copy(alpha = 0.6f * breathAlpha), accentColor.copy(alpha = 0.3f * breathAlpha))

        // Core light
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f * breathAlpha),
                    primaryColor.copy(alpha = 0.6f * breathAlpha),
                    primaryColor.copy(alpha = 0f)
                ),
                center = Offset(cx, cy - baseRadius * 0.1f),
                radius = baseRadius * 0.35f
            ),
            radius = baseRadius * 0.35f,
            center = Offset(cx, cy - baseRadius * 0.1f)
        )

        // Floating particles
        particles.forEach { p ->
            val px = cx + cos(p.angle * PI.toFloat() / 180f + particlePhase * p.speed) * baseRadius * p.radius * 2f
            val py = cy + sin(p.angle * PI.toFloat() / 180f + particlePhase * p.speed) * baseRadius * p.radius * 2f
            val flicker = (sin(particlePhase * 3f + p.angle) * 0.3f + 0.7f)
            drawCircle(
                color = primaryColor.copy(alpha = p.alpha * breathAlpha * flicker),
                radius = p.size,
                center = Offset(px, py)
            )
        }

        // Waveform bars at the bottom
        val barWidth = size.width / (waveBars.size * 2f)
        val barMaxHeight = baseRadius * 0.35f
        waveBars.forEachIndexed { i, base ->
            val barX = (i * 2 + 1) * barWidth
            val amplitude = when (state) {
                AvatarState.THINKING -> sin(wavePhase + i * 0.5f) * 0.5f + 0.5f
                AvatarState.SPEAKING -> abs(sin(wavePhase * 2f + i * 0.8f)) * (0.5f + base * 0.5f)
                AvatarState.IDLE -> sin(wavePhase + i * 0.3f) * 0.15f + 0.15f
            }
            val barH = barMaxHeight * amplitude
            val barColor = if (i % 3 == 0) accentColor else primaryColor
            drawRect(
                color = barColor.copy(alpha = 0.5f * breathAlpha),
                topLeft = Offset(barX, cy + baseRadius * 0.9f - barH / 2f),
                size = androidx.compose.ui.geometry.Size(barWidth * 0.7f, barH)
            )
        }

        // HUD rings (thin concentric)
        for (i in 1..3) {
            drawCircle(
                color = primaryColor.copy(alpha = 0.08f * breathAlpha),
                radius = baseRadius * (0.5f + i * 0.2f),
                center = Offset(cx, cy),
                style = Stroke(width = 0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f)))
            )
        }
    }
}

private fun DrawScope.drawOvalRing(cx: Float, cy: Float, radiusX: Float, radiusY: Float, color: Color, width: Float) {
    drawOval(
        color = color,
        topLeft = Offset(cx - radiusX, cy - radiusY),
        size = androidx.compose.ui.geometry.Size(radiusX * 2, radiusY * 2),
        style = Stroke(width = width)
    )
}

private fun DrawScope.drawOvalRingV(cx: Float, cy: Float, radius: Float, color: Color, width: Float) {
    drawOval(
        color = color,
        topLeft = Offset(cx - radius * 0.15f, cy - radius),
        size = androidx.compose.ui.geometry.Size(radius * 0.3f, radius * 2f),
        style = Stroke(width = width)
    )
}

private fun DrawScope.drawFemaleSilhouette(cx: Float, cy: Float, baseRadius: Float, color: Color, glowColor: Color) {
    val s = baseRadius * 0.012f
    val offsetY = cy - baseRadius * 0.15f

    // Head
    drawCircle(
        color = color,
        radius = baseRadius * 0.18f,
        center = Offset(cx, offsetY - baseRadius * 0.55f)
    )
    drawCircle(
        color = glowColor,
        radius = baseRadius * 0.22f,
        center = Offset(cx, offsetY - baseRadius * 0.55f),
        style = Stroke(width = 1f)
    )

    // Hair silhouette (wider top flowing down)
    val hairPath = Path().apply {
        moveTo(cx - baseRadius * 0.22f, offsetY - baseRadius * 0.6f)
        quadraticBezierTo(cx - baseRadius * 0.35f, offsetY - baseRadius * 0.8f, cx, offsetY - baseRadius * 0.82f)
        quadraticBezierTo(cx + baseRadius * 0.35f, offsetY - baseRadius * 0.8f, cx + baseRadius * 0.22f, offsetY - baseRadius * 0.6f)
        quadraticBezierTo(cx + baseRadius * 0.28f, offsetY - baseRadius * 0.35f, cx + baseRadius * 0.2f, offsetY - baseRadius * 0.2f)
        lineTo(cx - baseRadius * 0.2f, offsetY - baseRadius * 0.2f)
        quadraticBezierTo(cx - baseRadius * 0.28f, offsetY - baseRadius * 0.35f, cx - baseRadius * 0.22f, offsetY - baseRadius * 0.6f)
        close()
    }
    drawPath(hairPath, color.copy(alpha = color.alpha * 0.7f))

    // Neck
    drawRect(
        color = color,
        topLeft = Offset(cx - baseRadius * 0.05f, offsetY - baseRadius * 0.38f),
        size = androidx.compose.ui.geometry.Size(baseRadius * 0.1f, baseRadius * 0.12f)
    )

    // Shoulders + Torso
    val torsoPath = Path().apply {
        moveTo(cx - baseRadius * 0.05f, offsetY - baseRadius * 0.28f)
        quadraticBezierTo(cx - baseRadius * 0.3f, offsetY - baseRadius * 0.25f, cx - baseRadius * 0.28f, offsetY - baseRadius * 0.15f)
        lineTo(cx - baseRadius * 0.22f, offsetY + baseRadius * 0.15f)
        quadraticBezierTo(cx - baseRadius * 0.18f, offsetY + baseRadius * 0.25f, cx, offsetY + baseRadius * 0.2f)
        quadraticBezierTo(cx + baseRadius * 0.18f, offsetY + baseRadius * 0.25f, cx + baseRadius * 0.22f, offsetY + baseRadius * 0.15f)
        lineTo(cx + baseRadius * 0.28f, offsetY - baseRadius * 0.15f)
        quadraticBezierTo(cx + baseRadius * 0.3f, offsetY - baseRadius * 0.25f, cx + baseRadius * 0.05f, offsetY - baseRadius * 0.28f)
        close()
    }
    drawPath(torsoPath, color)

    // Left arm
    val leftArmPath = Path().apply {
        moveTo(cx - baseRadius * 0.28f, offsetY - baseRadius * 0.2f)
        quadraticBezierTo(cx - baseRadius * 0.55f, offsetY - baseRadius * 0.15f, cx - baseRadius * 0.65f, offsetY + baseRadius * 0.1f)
        quadraticBezierTo(cx - baseRadius * 0.68f, offsetY + baseRadius * 0.18f, cx - baseRadius * 0.62f, offsetY + baseRadius * 0.22f)
        quadraticBezierTo(cx - baseRadius * 0.52f, offsetY + baseRadius * 0.05f, cx - baseRadius * 0.25f, offsetY - baseRadius * 0.1f)
        close()
    }
    drawPath(leftArmPath, color)

    // Left hand
    drawCircle(
        color = color,
        radius = baseRadius * 0.06f,
        center = Offset(cx - baseRadius * 0.65f, offsetY + baseRadius * 0.2f)
    )
    // Left fingers
    for (f in 0..3) {
        val angle = -30f + f * 20f
        val fingerLen = baseRadius * 0.08f
        val fx = cx - baseRadius * 0.65f + cos(Math.toRadians(angle.toDouble())).toFloat() * fingerLen
        val fy = offsetY + baseRadius * 0.2f + sin(Math.toRadians(angle.toDouble())).toFloat() * fingerLen
        drawLine(
            color = color,
            start = Offset(cx - baseRadius * 0.65f, offsetY + baseRadius * 0.2f),
            end = Offset(fx, fy),
            strokeWidth = 1.5f
        )
    }

    // Right arm
    val rightArmPath = Path().apply {
        moveTo(cx + baseRadius * 0.28f, offsetY - baseRadius * 0.2f)
        quadraticBezierTo(cx + baseRadius * 0.55f, offsetY - baseRadius * 0.15f, cx + baseRadius * 0.65f, offsetY + baseRadius * 0.1f)
        quadraticBezierTo(cx + baseRadius * 0.68f, offsetY + baseRadius * 0.18f, cx + baseRadius * 0.62f, offsetY + baseRadius * 0.22f)
        quadraticBezierTo(cx + baseRadius * 0.52f, offsetY + baseRadius * 0.05f, cx + baseRadius * 0.25f, offsetY - baseRadius * 0.1f)
        close()
    }
    drawPath(rightArmPath, color)

    // Right hand
    drawCircle(
        color = color,
        radius = baseRadius * 0.06f,
        center = Offset(cx + baseRadius * 0.65f, offsetY + baseRadius * 0.2f)
    )
    // Right fingers
    for (f in 0..3) {
        val angle = 200f + f * 20f
        val fingerLen = baseRadius * 0.08f
        val fx = cx + baseRadius * 0.65f + cos(Math.toRadians(angle.toDouble())).toFloat() * fingerLen
        val fy = offsetY + baseRadius * 0.2f + sin(Math.toRadians(angle.toDouble())).toFloat() * fingerLen
        drawLine(
            color = color,
            start = Offset(cx + baseRadius * 0.65f, offsetY + baseRadius * 0.2f),
            end = Offset(fx, fy),
            strokeWidth = 1.5f
        )
    }

    // Hips
    val hipPath = Path().apply {
        moveTo(cx - baseRadius * 0.22f, offsetY + baseRadius * 0.15f)
        quadraticBezierTo(cx - baseRadius * 0.25f, offsetY + baseRadius * 0.3f, cx - baseRadius * 0.18f, offsetY + baseRadius * 0.45f)
        lineTo(cx - baseRadius * 0.1f, offsetY + baseRadius * 0.6f)
        quadraticBezierTo(cx, offsetY + baseRadius * 0.55f, cx + baseRadius * 0.1f, offsetY + baseRadius * 0.6f)
        lineTo(cx + baseRadius * 0.18f, offsetY + baseRadius * 0.45f)
        quadraticBezierTo(cx + baseRadius * 0.25f, offsetY + baseRadius * 0.3f, cx + baseRadius * 0.22f, offsetY + baseRadius * 0.15f)
        close()
    }
    drawPath(hipPath, color)

    // Glow outline on torso
    drawPath(torsoPath, glowColor, style = Stroke(width = 0.8f))
    drawPath(hipPath, glowColor, style = Stroke(width = 0.8f))

    // Scan lines effect (horizontal lines across the whole figure)
    for (i in 0..12) {
        val lineY = offsetY - baseRadius * 0.6f + (baseRadius * 1.2f / 12f) * i
        drawLine(
            color = color.copy(alpha = 0.08f),
            start = Offset(cx - baseRadius * 0.3f, lineY),
            end = Offset(cx + baseRadius * 0.3f, lineY),
            strokeWidth = 0.5f
        )
    }
}

private data class ParticleData(
    val angle: Float,
    val radius: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float
)
