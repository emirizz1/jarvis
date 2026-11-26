package com.jarvis.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin

/**
 * Gri Dairesel Ses Dalgasi - Jarvis
 * - Sen konusurken: DURUR (soluk gri halka)
 * - Jarvis konusurken: DALGALANIR (gri tonlarda pulse + dalga)
 */
class CircularWaveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var phase = 0f
    private var amp = 0.15f
    private var speaking = false
    private var volume = 0.5f

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2A2F3A")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9AA0A8") // gri
        style = Paint.Style.STROKE
        strokeWidth = 3.5f
        strokeCap = Paint.Cap.ROUND
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A9AA0A8")
        style = Paint.Style.STROKE
        strokeWidth = 14f
    }
    private val centerDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6B7280")
    }

    private var animator: ValueAnimator? = null

    init {
        animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 900
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { phase = it.animatedValue as Float; if (speaking || amp > 0.2f) invalidate() }
        }
        animator?.start()
    }

    fun setSpeaking(s: Boolean) {
        speaking = s
        ValueAnimator.ofFloat(amp, if(s) 1f else 0.15f).apply {
            duration = 350
            addUpdateListener { amp = it.animatedValue as Float; invalidate() }
        }.start()
        // center dot rengi
        centerDotPaint.color = if(s) Color.parseColor("#9AA0A8") else Color.parseColor("#4B5563")
    }

    fun setVolume(v: Int) { // 0-100
        if (speaking) volume = (v/100f).coerceIn(0.3f,1.2f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width/2f
        val cy = height/2f
        val baseR = minOf(width,height)*0.28f

        // dis halka (arka plan)
        canvas.drawCircle(cx, cy, baseR, bgPaint.apply { alpha = 40 })

        // 3 katmanli dalga halkasi
        for (layer in 0..2) {
            val path = Path()
            val layerAmp = amp * (1f - layer*0.25f) * volume
            val layerR = baseR + layer*10f

            for (a in 0..360 step 6) {
                val rad = Math.toRadians(a.toDouble())
                // dalga: sinüs + phase
                val wave = kotlin.math.sin((a*2 + phase + layer*40)*Math.PI/180) * 0.5 +
                           kotlin.math.sin((a*3 - phase*0.7)*Math.PI/180) * 0.3
                val r = layerR + wave*(18f * layerAmp)
                val x = cx + cos(rad).toFloat()*r
                val y = cy + sin(rad).toFloat()*r
                if (a==0) path.moveTo(x,y) else path.lineTo(x,y)
            }
            path.close()
            val p = if (layer==0) wavePaint else glowPaint
            p.alpha = ((if(speaking) 220 else 90) * (1f - layer*0.3f)).toInt())
            p.strokeWidth = if(layer==0) 3.5f else 10f - layer*2f
            canvas.drawPath(path, p)
        }

        // orta nokta
        canvas.drawCircle(cx, cy, 8f + amp*4f, centerDotPaint)
        // ic pulse
        if (speaking) {
            canvas.drawCircle(cx, cy, baseR*0.45f* volume, Paint().apply {
                color = Color.parseColor("#0F9AA0A8"); style = Paint.Style.STROKE; strokeWidth=2f
            })
        }
    }

    override fun onDetachedFromWindow() { animator?.cancel(); super.onDetachedFromWindow() }
}
