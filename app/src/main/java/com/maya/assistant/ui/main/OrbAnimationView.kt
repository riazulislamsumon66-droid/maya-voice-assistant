package com.maya.assistant.ui.main

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.*

class OrbAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // State
    private var isসক্রিয় = false
    private var isবলছে… = false
    private var isভাবছে… = false
    private var isPulsating = false
    private var speakAmplitude = 0f

    // Animation
    private var rotationAngle = 0f
    private var pulseScale = 1f
    private var glowAlpha = 180
    private var waveবন্ধset = 0f
    private var thinkingAngle = 0f

    // Animators
    private val rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 4000
        repeatগণনা = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addআপডেটListener {
            rotationAngle = it.animatedValue as Float
            invalidate()
        }
    }

    private val pulseAnimator = ValueAnimator.ofFloat(1f, 1.15f, 1f).apply {
        duration = 1500
        repeatগণনা = ValueAnimator.INFINITE
        interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        addআপডেটListener {
            pulseScale = it.animatedValue as Float
            invalidate()
        }
    }

    private val glowAnimator = ValueAnimator.ofInt(120, 220, 120).apply {
        duration = 2000
        repeatগণনা = ValueAnimator.INFINITE
        addআপডেটListener {
            glowAlpha = it.animatedValue as Int
            invalidate()
        }
    }

    private val waveAnimator = ValueAnimator.ofFloat(0f, (2 * Math.PI).toFloat()).apply {
        duration = 1200
        repeatগণনা = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addআপডেটListener {
            waveবন্ধset = it.animatedValue as Float
            invalidate()
        }
    }

    private val thinkingAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 1000
        repeatগণনা = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addআপডেটListener {
            thinkingAngle = it.animatedValue as Float
            invalidate()
        }
    }

    // Paints
    private val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STRঠিক আছেE
        strokeWidth = 3f
    }
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STRঠিক আছেE
        strokeWidth = 2.5f
    }
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // রঙs
    private val coreরঙ1 = রঙ.parseরঙ("#FF1744")
    private val coreরঙ2 = রঙ.parseরঙ("#D500F9")
    private val glowরঙ = রঙ.parseরঙ("#FF1744")
    private val ringরঙ = রঙ.parseরঙ("#FF6D6D")
    private val activeরঙ = রঙ.parseরঙ("#FF1744")
    private val speakরঙ = রঙ.parseরঙ("#E040FB")
    private val thinkরঙ = রঙ.parseরঙ("#40C4FF")

    // Particles
    private data class Particle(var angle: Float, var radius: Float, var size: Float, var alpha: Int)
    private val particles = (0..12).map {
        Particle(
            angle = (it * 360f / 12f),
            radius = 0f,
            size = (4..8).random().toFloat(),
            alpha = (100..255).random()
        )
    }

    init {
        startIdleAnimation()
    }

    private fun startIdleAnimation() {
        pulseAnimator.start()
        glowAnimator.start()
    }

    fun setসক্রিয়(active: Boolean) {
        isসক্রিয় = active
        if (active) {
            rotationAnimator.start()
            waveAnimator.start()
        } else {
            rotationAnimator.cancel()
            waveAnimator.cancel()
        }
        invalidate()
    }

    fun setবলছে…(speaking: Boolean) {
        isবলছে… = speaking
        if (speaking) {
            waveAnimator.duration = 600
            waveAnimator.start()
        } else {
            waveAnimator.duration = 1200
        }
        invalidate()
    }

    fun setভাবছে…(thinking: Boolean) {
        isভাবছে… = thinking
        if (thinking) {
            thinkingAnimator.start()
        } else {
            thinkingAnimator.cancel()
        }
        invalidate()
    }

    fun setPulsating(pulsating: Boolean) {
        isPulsating = pulsating
        invalidate()
    }

    fun setAmplitude(amplitude: Float) {
        speakAmplitude = amplitude.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val baseRadius = minOf(cx, cy) * 0.55f

        canvas.save()
        canvas.scale(pulseScale, pulseScale, cx, cy)

        // Glow layer
        drawGlow(canvas, cx, cy, baseRadius)

        // Core orb
        drawCoreOrb(canvas, cx, cy, baseRadius)

        // Rings
        drawRings(canvas, cx, cy, baseRadius)

        // Wave effect (when active/speaking)
        if (isসক্রিয় || isবলছে…) {
            drawWaves(canvas, cx, cy, baseRadius)
        }

        // ভাবছে… indicator
        if (isভাবছে…) {
            drawভাবছে…Arc(canvas, cx, cy, baseRadius)
        }

        // Particles
        if (isসক্রিয় || isবলছে…) {
            drawParticles(canvas, cx, cy, baseRadius)
        }

        canvas.restore()

        // Inner glow pulse
        drawInnerহাইghlight(canvas, cx, cy, baseRadius * pulseScale)
    }

    private fun drawGlow(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val glowRadius = radius * 1.6f
        val color = when {
            isবলছে… -> speakরঙ
            isভাবছে… -> thinkরঙ
            isসক্রিয় -> activeরঙ
            else -> glowরঙ
        }
        val shader = RadialGradient(
            cx, cy, glowRadius,
            intArrayOf(
                রঙ.argb(glowAlpha / 3, রঙ.red(color), রঙ.green(color), রঙ.blue(color)),
                রঙ.argb(0, রঙ.red(color), রঙ.green(color), রঙ.blue(color))
            ),
            floatArrayOf(0.3f, 1f),
            Shader.TileMode.CLAMP
        )
        glowPaint.shader = shader
        canvas.drawCircle(cx, cy, glowRadius, glowPaint)
    }

    private fun drawCoreOrb(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val color1 = when {
            isবলছে… -> রঙ.parseরঙ("#E040FB")
            isভাবছে… -> রঙ.parseরঙ("#40C4FF")
            isসক্রিয় -> রঙ.parseরঙ("#FF1744")
            else -> রঙ.parseরঙ("#B71C1C")
        }
        val color2 = when {
            isবলছে… -> রঙ.parseরঙ("#FF1744")
            isভাবছে… -> রঙ.parseরঙ("#00B0FF")
            isসক্রিয় -> রঙ.parseরঙ("#D500F9")
            else -> রঙ.parseরঙ("#880E4F")
        }

        val shader = RadialGradient(
            cx - radius * 0.3f, cy - radius * 0.3f, radius,
            intArrayOf(color1, color2),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        orbPaint.shader = shader
        canvas.drawCircle(cx, cy, radius, orbPaint)
    }

    private fun drawRings(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val ringগণনা = 3
        for (i in 0 until ringগণনা) {
            val r = radius + (i + 1) * 18f
            val alpha = (255 - i * 60).coerceAtLeast(50)
            ringPaint.color = রঙ.argb(alpha, 255, 80, 80)
            ringPaint.strokeWidth = (3f - i * 0.8f).coerceAtLeast(1f)

            canvas.save()
            canvas.rotate(rotationAngle + i * 30f, cx, cy)
            val oval = RectF(cx - r, cy - r, cx + r, cy + r)
            canvas.drawArc(oval, 0f, 280f, false, ringPaint)
            canvas.restore()
        }
    }

    private fun drawWaves(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val waveগণনা = if (isবলছে…) 8 else 5
        val amplitude = if (isবলছে…) radius * 0.3f * (0.5f + speakAmplitude) else radius * 0.15f
        val waveRadius = radius + 25f

        val path = Path()
        val points = 180
        for (ring in 0..1) {
            path.reset()
            val r = waveRadius + ring * 20f
            for (j in 0..points) {
                val angle = (j * 360f / points).toRadians()
                val wave = amplitude * sin(waveগণনা * angle + waveবন্ধset + ring * 1.2f)
                val x = cx + (r + wave) * cos(angle)
                val y = cy + (r + wave) * sin(angle)
                if (j == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            wavePaint.color = রঙ.argb(
                if (ring == 0) 200 else 120,
                if (isবলছে…) 224 else 255,
                if (isবলছে…) 64 else 30,
                if (isবলছে…) 251 else 50
            )
            wavePaint.strokeWidth = if (ring == 0) 2.5f else 1.5f
            canvas.drawPath(path, wavePaint)
        }
    }

    private fun drawভাবছে…Arc(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val arcRadius = radius + 40f
        val oval = RectF(cx - arcRadius, cy - arcRadius, cx + arcRadius, cy + arcRadius)
        ringPaint.color = রঙ.parseরঙ("#40C4FF")
        ringPaint.strokeWidth = 4f
        canvas.save()
        canvas.rotate(thinkingAngle, cx, cy)
        canvas.drawArc(oval, 0f, 120f, false, ringPaint)
        canvas.drawArc(oval, 180f, 120f, false, ringPaint)
        canvas.restore()
    }

    private fun drawParticles(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        particles.forEach { p ->
            p.angle = (p.angle + 0.8f) % 360f
            val pRadius = radius + 30f + 20f * sin(p.angle.toRadians() * 3)
            val x = cx + pRadius * cos(p.angle.toRadians())
            val y = cy + pRadius * sin(p.angle.toRadians())
            val color = if (isবলছে…) রঙ.parseরঙ("#E040FB") else রঙ.parseরঙ("#FF6D6D")
            particlePaint.color = রঙ.argb(p.alpha, রঙ.red(color), রঙ.green(color), রঙ.blue(color))
            canvas.drawCircle(x, y, p.size, particlePaint)
        }
    }

    private fun drawInnerহাইghlight(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val highlightShader = RadialGradient(
            cx - radius * 0.25f, cy - radius * 0.25f, radius * 0.5f,
            intArrayOf(রঙ.argb(120, 255, 255, 255), রঙ.TRANSPARENT),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = highlightShader }
        canvas.drawCircle(cx - radius * 0.15f, cy - radius * 0.15f, radius * 0.45f, highlightPaint)
    }

    private fun Float.toRadians() = this * (Math.PI / 180f).toFloat()

    // ── Convenience state methods ──────────────────────────────
    fun setশুনছে…() {
        setসক্রিয়(true); setবলছে…(false); setভাবছে…(false); setPulsating(false)
    }
    fun setবলছে…() {
        setসক্রিয়(true); setবলছে…(true); setভাবছে…(false); setPulsating(false)
    }
    fun setভাবছে…() {
        setসক্রিয়(true); setবলছে…(false); setভাবছে…(true); setPulsating(false)
    }
    fun setIdle() {
        setসক্রিয়(false); setবলছে…(false); setভাবছে…(false); setPulsating(true)
    }

    override fun onDetachedFromWindow() {
        rotationAnimator.cancel()
        pulseAnimator.cancel()
        glowAnimator.cancel()
        waveAnimator.cancel()
        thinkingAnimator.cancel()
        super.onDetachedFromWindow()
    }
}
