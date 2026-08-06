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
    primaryColor: Color = Color(0xFF00BFFF),
    accentColor: Color = Color(0xFF8B5CF6),
    glowColor: Color = Color(0xFF00BFFF)
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
        val baseRadius = minOf(cx, cy) * 0.42f

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
        val sillState = state
        drawFemaleSilhouette(cx, cy, baseRadius, primaryColor.copy(alpha = 0.6f * breathAlpha), accentColor.copy(alpha = 0.3f * breathAlpha), sillState, wavePhase)

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

        // HUD frame — cyberpunk targeting reticle with corner brackets
        val frameL = cx - baseRadius * 1.55f
        val frameR = cx + baseRadius * 1.55f
        val frameT = cy - baseRadius * 1.7f
        val frameB = cy + baseRadius * 1.25f
        val bracket = baseRadius * 0.22f
        val frameAlpha = 0.55f * breathAlpha
        val frameStroke = Stroke(width = 1.5f)
        // Top-left corner
        drawLine(primaryColor.copy(alpha = frameAlpha), Offset(frameL, frameT + bracket), Offset(frameL, frameT), frameStroke.width)
        drawLine(primaryColor.copy(alpha = frameAlpha), Offset(frameL, frameT), Offset(frameL + bracket, frameT), frameStroke.width)
        // Top-right corner
        drawLine(primaryColor.copy(alpha = frameAlpha), Offset(frameR - bracket, frameT), Offset(frameR, frameT), frameStroke.width)
        drawLine(primaryColor.copy(alpha = frameAlpha), Offset(frameR, frameT), Offset(frameR, frameT + bracket), frameStroke.width)
        // Bottom-left corner
        drawLine(primaryColor.copy(alpha = frameAlpha), Offset(frameL, frameB - bracket), Offset(frameL, frameB), frameStroke.width)
        drawLine(primaryColor.copy(alpha = frameAlpha), Offset(frameL, frameB), Offset(frameL + bracket, frameB), frameStroke.width)
        // Bottom-right corner
        drawLine(primaryColor.copy(alpha = frameAlpha), Offset(frameR - bracket, frameB), Offset(frameR, frameB), frameStroke.width)
        drawLine(primaryColor.copy(alpha = frameAlpha), Offset(frameR, frameB), Offset(frameR, frameB - bracket), frameStroke.width)
        // Accent dots at corners
        listOf(Offset(frameL, frameT), Offset(frameR, frameT), Offset(frameL, frameB), Offset(frameR, frameB)).forEach { corner ->
            drawCircle(accentColor.copy(alpha = 0.7f * breathAlpha), radius = 2.5f, center = corner)
        }

        // Name label "S.A.S.H.A." below avatar using nativeCanvas
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(
                    (255 * breathAlpha * 0.85f).toInt(),
                    0x00, 0xBF, 0xFF
                )
                textSize = baseRadius * 0.16f
                typeface = android.graphics.Typeface.MONOSPACE
                textAlign = android.graphics.Paint.Align.CENTER
                letterSpacing = 0.25f
            }
            drawText("S . A . S . H . A .", cx, frameB - baseRadius * 0.06f, paint)
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

private fun DrawScope.drawFemaleSilhouette(cx: Float, cy: Float, baseRadius: Float, color: Color, glowColor: Color, state: AvatarState = AvatarState.IDLE, wavePhase: Float = 0f) {
    val s = baseRadius * 0.012f
    val offsetY = cy - baseRadius * 0.15f

    // --- HEAD ---
    val headCY = offsetY - baseRadius * 0.55f
    drawCircle(color = color, radius = baseRadius * 0.18f, center = Offset(cx, headCY))
    drawCircle(color = glowColor, radius = baseRadius * 0.22f, center = Offset(cx, headCY), style = Stroke(width = 1f))

    // --- HAIR ---
    val hairPath = Path().apply {
        moveTo(cx - baseRadius * 0.22f, headCY + baseRadius * 0.02f)
        quadraticBezierTo(cx - baseRadius * 0.35f, headCY - baseRadius * 0.25f, cx, headCY - baseRadius * 0.27f)
        quadraticBezierTo(cx + baseRadius * 0.35f, headCY - baseRadius * 0.25f, cx + baseRadius * 0.22f, headCY + baseRadius * 0.02f)
        quadraticBezierTo(cx + baseRadius * 0.28f, headCY + baseRadius * 0.2f, cx + baseRadius * 0.2f, headCY + baseRadius * 0.35f)
        lineTo(cx - baseRadius * 0.2f, headCY + baseRadius * 0.35f)
        quadraticBezierTo(cx - baseRadius * 0.28f, headCY + baseRadius * 0.2f, cx - baseRadius * 0.22f, headCY + baseRadius * 0.02f)
        close()
    }
    drawPath(hairPath, color.copy(alpha = color.alpha * 0.7f))

    // --- EYES ---
    val eyeY = headCY + baseRadius * 0.02f
    val eyeSpacing = baseRadius * 0.08f
    val eyeSize = baseRadius * 0.035f
    // Left eye
    drawCircle(color = glowColor.copy(alpha = 0.9f), radius = eyeSize, center = Offset(cx - eyeSpacing, eyeY))
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = eyeSize * 0.4f, center = Offset(cx - eyeSpacing, eyeY))
    // Right eye
    drawCircle(color = glowColor.copy(alpha = 0.9f), radius = eyeSize, center = Offset(cx + eyeSpacing, eyeY))
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = eyeSize * 0.4f, center = Offset(cx + eyeSpacing, eyeY))

    // --- MOUTH ---
    val mouthY = eyeY + baseRadius * 0.1f
    val mouthWidth = baseRadius * 0.08f
    val mouthOpen = when (state) {
        AvatarState.SPEAKING -> abs(sin(wavePhase * 3f)) * 0.5f + 0.2f
        else -> 0.08f
    }
    // Lips - upper
    drawArc(
        color = color,
        startAngle = 180f, sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - mouthWidth, mouthY - baseRadius * 0.02f),
        size = androidx.compose.ui.geometry.Size(mouthWidth * 2f, baseRadius * 0.04f),
        style = Stroke(width = 1.2f)
    )
    // Lips - lower / mouth opening
    drawArc(
        color = glowColor.copy(alpha = 0.6f),
        startAngle = 0f, sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - mouthWidth * mouthOpen, mouthY - baseRadius * 0.01f),
        size = androidx.compose.ui.geometry.Size(mouthWidth * 2f * mouthOpen, baseRadius * 0.04f * mouthOpen),
        style = Stroke(width = 0.8f)
    )

    // --- NECK ---
    val neckTop = headCY + baseRadius * 0.17f
    drawRect(color = color, topLeft = Offset(cx - baseRadius * 0.05f, neckTop), size = androidx.compose.ui.geometry.Size(baseRadius * 0.1f, baseRadius * 0.12f))

    // --- TORSO ---
    val shoulderY = neckTop + baseRadius * 0.12f
    val waistY = shoulderY + baseRadius * 0.35f
    val hipY = waistY + baseRadius * 0.15f
    val torsoPath = Path().apply {
        moveTo(cx - baseRadius * 0.05f, shoulderY)
        quadraticBezierTo(cx - baseRadius * 0.3f, shoulderY + baseRadius * 0.03f, cx - baseRadius * 0.28f, shoulderY + baseRadius * 0.12f)
        lineTo(cx - baseRadius * 0.2f, waistY)
        quadraticBezierTo(cx - baseRadius * 0.18f, waistY + baseRadius * 0.08f, cx - baseRadius * 0.22f, hipY)
        quadraticBezierTo(cx - baseRadius * 0.25f, hipY + baseRadius * 0.15f, cx - baseRadius * 0.18f, hipY + baseRadius * 0.3f)
        lineTo(cx - baseRadius * 0.1f, hipY + baseRadius * 0.45f)
        quadraticBezierTo(cx, hipY + baseRadius * 0.4f, cx + baseRadius * 0.1f, hipY + baseRadius * 0.45f)
        lineTo(cx + baseRadius * 0.18f, hipY + baseRadius * 0.3f)
        quadraticBezierTo(cx + baseRadius * 0.25f, hipY + baseRadius * 0.15f, cx + baseRadius * 0.22f, hipY)
        quadraticBezierTo(cx + baseRadius * 0.18f, waistY + baseRadius * 0.08f, cx + baseRadius * 0.2f, waistY)
        lineTo(cx + baseRadius * 0.28f, shoulderY + baseRadius * 0.12f)
        quadraticBezierTo(cx + baseRadius * 0.3f, shoulderY + baseRadius * 0.03f, cx + baseRadius * 0.05f, shoulderY)
        close()
    }
    drawPath(torsoPath, color)
    drawPath(torsoPath, glowColor, style = Stroke(width = 0.8f))

    // --- ARMS ---
    val armSwing = sin(wavePhase * 0.5f) * 0.03f * baseRadius
    // Left arm
    val leftArmPath = Path().apply {
        moveTo(cx - baseRadius * 0.28f, shoulderY + baseRadius * 0.08f)
        quadraticBezierTo(cx - baseRadius * 0.38f, shoulderY + baseRadius * 0.2f, cx - baseRadius * 0.35f + armSwing, shoulderY + baseRadius * 0.5f)
        lineTo(cx - baseRadius * 0.3f + armSwing, hipY + baseRadius * 0.25f)
    }
    drawPath(leftArmPath, color, style = Stroke(width = baseRadius * 0.06f, cap = StrokeCap.Round))
    // Right arm
    val rightArmPath = Path().apply {
        moveTo(cx + baseRadius * 0.28f, shoulderY + baseRadius * 0.08f)
        quadraticBezierTo(cx + baseRadius * 0.38f, shoulderY + baseRadius * 0.2f, cx + baseRadius * 0.35f - armSwing, shoulderY + baseRadius * 0.5f)
        lineTo(cx + baseRadius * 0.3f - armSwing, hipY + baseRadius * 0.25f)
    }
    drawPath(rightArmPath, color, style = Stroke(width = baseRadius * 0.06f, cap = StrokeCap.Round))

    // --- HANDS with FINGERS ---
    val handCY = hipY + baseRadius * 0.25f
    // Left hand
    val lhx = cx - baseRadius * 0.3f + armSwing
    drawCircle(color = color.copy(alpha = 0.8f), radius = baseRadius * 0.04f, center = Offset(lhx, handCY))
    // Fingers
    val fingerLen = baseRadius * 0.04f
    for (i in -2..2) {
        val angle = i * 20f * PI.toFloat() / 180f
        val fx = lhx + cos(angle + PI.toFloat()/2f) * fingerLen
        val fy = handCY + sin(angle + PI.toFloat()/2f) * fingerLen
        drawLine(color = color.copy(alpha = 0.6f), start = Offset(lhx, handCY), end = Offset(fx, fy), strokeWidth = 1.5f)
    }
    // Right hand
    val rhx = cx + baseRadius * 0.3f - armSwing
    drawCircle(color = color.copy(alpha = 0.8f), radius = baseRadius * 0.04f, center = Offset(rhx, handCY))
    for (i in -2..2) {
        val angle = i * 20f * PI.toFloat() / 180f
        val fx = rhx + cos(PI.toFloat() - angle + PI.toFloat()/2f) * fingerLen
        val fy = handCY + sin(PI.toFloat() - angle + PI.toFloat()/2f) * fingerLen
        drawLine(color = color.copy(alpha = 0.6f), start = Offset(rhx, handCY), end = Offset(fx, fy), strokeWidth = 1.5f)
    }

    // --- LEGS ---
    val legTop = hipY + baseRadius * 0.35f
    val kneeY = legTop + baseRadius * 0.25f
    val footY = kneeY + baseRadius * 0.25f
    val legSwing = sin(wavePhase * 0.7f) * 0.02f * baseRadius
    // Left leg
    val leftLegPath = Path().apply {
        moveTo(cx - baseRadius * 0.1f, legTop)
        quadraticBezierTo(cx - baseRadius * 0.12f + legSwing, kneeY, cx - baseRadius * 0.08f + legSwing, footY)
    }
    drawPath(leftLegPath, color, style = Stroke(width = baseRadius * 0.08f, cap = StrokeCap.Round))
    // Right leg
    val rightLegPath = Path().apply {
        moveTo(cx + baseRadius * 0.1f, legTop)
        quadraticBezierTo(cx + baseRadius * 0.12f - legSwing, kneeY, cx + baseRadius * 0.08f - legSwing, footY)
    }
    drawPath(rightLegPath, color, style = Stroke(width = baseRadius * 0.08f, cap = StrokeCap.Round))

    // --- FEET ---
    drawCircle(color = color.copy(alpha = 0.7f), radius = baseRadius * 0.03f, center = Offset(cx - baseRadius * 0.08f + legSwing, footY))
    drawCircle(color = color.copy(alpha = 0.7f), radius = baseRadius * 0.03f, center = Offset(cx + baseRadius * 0.08f - legSwing, footY))

    // --- GLOW OUTLINE on torso/hips ---
    drawPath(torsoPath, glowColor, style = Stroke(width = 0.8f))

    // --- SCAN LINES ---
    for (i in 0..16) {
        val lineY = headCY - baseRadius * 0.25f + (baseRadius * 2f / 16f) * i
        drawLine(
            color = color.copy(alpha = 0.06f),
            start = Offset(cx - baseRadius * 0.35f, lineY),
            end = Offset(cx + baseRadius * 0.35f, lineY),
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
