package com.fpsmeter.app

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Choreographer
import android.widget.LinearLayout
import android.widget.TextView
import java.text.DecimalFormat

class FPSOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), Choreographer.FrameCallback {

    private val fpsTextView: TextView
    private val choreographer: Choreographer = Choreographer.getInstance()
    private val handler = Handler(Looper.getMainLooper())
    
    private var frameCount = 0
    private var lastFrameTime = 0L
    private var fps = 0.0
    private val fpsFormat = DecimalFormat("0.0")
    
    // FPS calculation variables
    private val frameTimes = mutableListOf<Long>()
    private val maxFrameHistory = 60 // Keep last 60 frames for better accuracy
    
    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#80000000")) // Semi-transparent black
        setPadding(16, 8, 16, 8)
        
        // Create FPS text view
        fpsTextView = TextView(context).apply {
            text = "FPS: --"
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
        }
        
        addView(fpsTextView)
    }

    fun startFPSMonitoring() {
        choreographer.postFrameCallback(this)
        lastFrameTime = System.nanoTime()
    }

    fun stopFPSMonitoring() {
        choreographer.removeFrameCallback(this)
        frameTimes.clear()
    }

    override fun doFrame(frameTimeNanos: Long) {
        frameCount++
        
        // Add current frame time to history
        frameTimes.add(frameTimeNanos)
        
        // Keep only recent frames
        if (frameTimes.size > maxFrameHistory) {
            frameTimes.removeAt(0)
        }
        
        // Calculate FPS every 30 frames or every second
        if (frameCount % 30 == 0 || frameTimeNanos - lastFrameTime >= 1_000_000_000L) {
            calculateFPS()
            updateFPSDisplay()
            lastFrameTime = frameTimeNanos
        }
        
        // Schedule next frame callback
        choreographer.postFrameCallback(this)
    }

    private fun calculateFPS() {
        if (frameTimes.size < 2) {
            fps = 0.0
            return
        }
        
        val startTime = frameTimes.first()
        val endTime = frameTimes.last()
        val timeDifference = endTime - startTime
        
        if (timeDifference > 0) {
            // Calculate FPS based on frame time differences
            fps = (frameTimes.size - 1) * 1_000_000_000.0 / timeDifference
            
            // Clamp FPS to reasonable values
            fps = fps.coerceIn(0.0, 240.0)
        }
    }

    private fun updateFPSDisplay() {
        handler.post {
            val fpsText = "FPS: ${fpsFormat.format(fps)}"
            fpsTextView.text = fpsText
            
            // Change color based on FPS
            val color = when {
                fps >= 55 -> Color.GREEN
                fps >= 30 -> Color.YELLOW
                else -> Color.RED
            }
            fpsTextView.setTextColor(color)
        }
    }
}