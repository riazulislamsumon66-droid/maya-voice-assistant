package com.maya.assistant.ui.main

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.*

enum class OrbState { IDLE, LISTENING, SPEAKING, THINKING }

class OrbAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var orbState = OrbState.IDLE
    private var rotationAngle = 0f
    private var waveOffset = 0f
    private var pulseScale = 1f
    private var glowAlpha = 120
    private var thinkingAngle = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    // Colors
    private val idleColor1 = Color.parseColor("#B71C1C")
    private val idleColor2 = Color.parseColor("#880E4F")
    private val activeColor1 = Color.parseColor("#FF1744")
    private val activeColor2 = Color.parseColor("#D500F9")
    private val speakingColor1 = Color.parseColor("#E040FB")
    private val speakingColor2 = Color.parseColor("#FF1744")
    private val thinkingColor1 = Color.parseColor("#40C4FF")
    private val thinkingColor2 = Color.parseColor("#00B0FF")

    private var rotationAnimator: ValueAnimator? = null
    private var waveAnimator: ValueAnimator? = null
    private var pulseAnimator: ValueAnimator? = null
    private var glowAnimator: ValueAnimator? = null
    private var thinkingAnimator: ValueAnimator? = null

    init {
        startIdleAnimation()
    }

    fun setState(state: OrbState) {
        orbState = state
        stopAllAnimators()
        when (state) {
            OrbState.IDLE -> startIdleAnimation()
            OrbState.LISTENING -> startListeningAnimation()
            OrbState.SPEAKING -> startSpeakingAnimation()
            OrbState.THINKING -> startThinkingAnimation()
        }
    }

    private fun stopAllAnimators() {
        rotationAnimator?.cancel()
        waveAnimator?.cancel()
        pulseAnimator?.cancel()
        glowAnimator?.cancel()
        thinkingAnimator?.cancel()
    }

    private fun startIdleAnimation() {
        pulseAnimator = ValueAnimator.ofFloat(1f, 1.15f, 1f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { pulseScale = it.animatedValue as Float; invalidate() }
            start()
        }
        glowAnimator = ValueAnimator.ofInt(120, 220, 120).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { glowAlpha = it.animatedValue as Int; invalidate() }
            start()
        }
    }

    private fun startListeningAnimation() {
        startRotation()
        startWaves()
    }

    private fun startSpeakingAnimation() {
        startRotation()
        startWaves()
    }

    private fun startThinkingAnimation() {
        thinkingAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { thinkingAngle = it.animatedValue as Float; invalidate() }
            start()
        }
    }

    private fun startRotation() {
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { rotationAngle = it.animatedValue as Float; invalidate() }
            start()
        }
    }

    private fun startWaves() {
        waveAnimator = ValueAnimator.ofFloat(0f, 2f * PI.toFloat()).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { waveOffset = it.animatedValue as Float; invalidate() }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val baseRadius = minOf(cx, cy) * 0.7f
        val radius = baseRadius * pulseScale

        // 1. Radial glow
        paint.shader = RadialGradient(cx, cy, radius * 1.6f, intArrayOf(
            getGlowColor(), Color.TRANSPARENT
        ), floatArrayOf(0.3f, 1f), Shader.TileMode.CLAMP)
        paint.alpha = glowAlpha
        canvas.drawCircle(cx, cy, radius * 1.6f, paint)
        paint.alpha = 255
        paint.shader = null

        // 2. Core orb
        val coreColors = getCoreColors()
        paint.shader = RadialGradient(
            cx - radius * 0.3f, cy - radius * 0.3f, radius,
            coreColors, floatArrayOf(0f, 0.7f, 1f), Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, radius, paint)
        paint.shader = null

        // 3. Rotating rings
        if (orbState == OrbState.LISTENING || orbState == OrbState.SPEAKING) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            for (i in 1..3) {
                val ringRadius = radius * (1.1f + i * 0.15f)
                paint.color = if (i % 2 == 0) activeColor1 else activeColor2
                paint.alpha = 80 + i * 30
                rectF.set(cx - ringRadius, cy - ringRadius, cx + ringRadius, cy + ringRadius)
                canvas.drawArc(rectF, rotationAngle + i * 60, 120f, false, paint)
            }
            paint.alpha = 255
            paint.style = Paint.Style.FILL
        }

        // 4. Wave rings
        if (orbState == OrbState.LISTENING || orbState == OrbState.SPEAKING) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.5f
            paint.color = activeColor1
            paint.alpha = 60
            for (i in 0 until 3) {
                val waveRadius = radius * (1.3f + i * 0.2f)
                val path = Path()
                for (angle in 0..360 step 5) {
                    val rad = Math.toRadians(angle.toDouble())
                    val wave = sin(rad * 3 + waveOffset + i) * 5
                    val r = (waveRadius + wave).toFloat()
                    val x = cx + r * cos(rad).toFloat()
                    val y = cy + r * sin(rad).toFloat()
                    if (angle == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                canvas.drawPath(path, paint)
            }
            paint.alpha = 255
            paint.style = Paint.Style.FILL
        }

        // 5. Thinking arc
        if (orbState == OrbState.THINKING) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            paint.color = thinkingColor1
            rectF.set(cx - radius * 1.2f, cy - radius * 1.2f, cx + radius * 1.2f, cy + radius * 1.2f)
            canvas.drawArc(rectF, thinkingAngle, 120f, false, paint)
            canvas.drawArc(rectF, thinkingAngle + 180, 120f, false, paint)
            paint.style = Paint.Style.FILL
        }

        // 6. Particles
        if (orbState == OrbState.SPEAKING || orbState == OrbState.LISTENING) {
            paint.color = activeColor2
            for (i in 0 until 12) {
                val angle = (rotationAngle + i * 30) * PI.toFloat() / 180f
                val dist = radius * 1.4f
                val px = cx + cos(angle) * dist
                val py = cy + sin(angle) * dist
                paint.alpha = 100 + (i * 10)
                canvas.drawCircle(px, py, 3f, paint)
            }
            paint.alpha = 255
        }

        // 7. Inner highlight
        paint.shader = RadialGradient(
            cx - radius * 0.3f, cy - radius * 0.3f, radius * 0.5f,
            intArrayOf(Color.argb(60, 255, 255, 255), Color.TRANSPARENT),
            floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, radius, paint)
        paint.shader = null
    }

    private fun getGlowColor(): Int {
        return when (orbState) {
            OrbState.IDLE -> idleColor1
            OrbState.LISTENING -> activeColor1
            OrbState.SPEAKING -> speakingColor1
            OrbState.THINKING -> thinkingColor1
        }
    }

    private fun getCoreColors(): IntArray {
        return when (orbState) {
            OrbState.IDLE -> intArrayOf(idleColor1, idleColor2, Color.parseColor("#1A0000"))
            OrbState.LISTENING -> intArrayOf(activeColor1, activeColor2, Color.parseColor("#1A0000"))
            OrbState.SPEAKING -> intArrayOf(speakingColor1, speakingColor2, Color.parseColor("#1A0000"))
            OrbState.THINKING -> intArrayOf(thinkingColor1, thinkingColor2, Color.parseColor("#001A1A"))
        }
    }
}
