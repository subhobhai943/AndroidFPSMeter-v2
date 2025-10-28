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
import android.util.Log

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
    private var isMonitoring = false
    
    private val frameTimes = mutableListOf<Long>()
    private val maxFrameHistory = 60
    
    companion object {
        private const val TAG = "FPSMeter_OverlayView"
    }
    
    init {
        try {
            orientation = VERTICAL
            setBackgroundColor(Color.parseColor("#CC000000"))
            setPadding(12, 6, 12, 6)
            
            fpsTextView = TextView(context).apply {
                text = "FPS: --"
                setTextColor(Color.WHITE)
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setShadowLayer(2f, 1f, 1f, Color.BLACK)
            }
            
            addView(fpsTextView)
            Log.d(TAG, "FPSOverlayView initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FPSOverlayView", e)
            throw e
        }
    }

    fun startFPSMonitoring() {
        Log.d(TAG, "Starting FPS monitoring")
        
        try {
            if (isMonitoring) return
            choreographer.postFrameCallback(this)
            lastFrameTime = System.nanoTime()
            isMonitoring = true
            frameCount = 0
            frameTimes.clear()
            handler.post {
                fpsTextView.text = "FPS: Starting..."
                fpsTextView.setTextColor(Color.YELLOW)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting FPS monitoring", e)
            isMonitoring = false
        }
    }

    fun stopFPSMonitoring() {
        Log.d(TAG, "Stopping FPS monitoring")
        
        try {
            if (!isMonitoring) return
            choreographer.removeFrameCallback(this)
            isMonitoring = false
            frameTimes.clear()
            handler.post {
                fpsTextView.text = "FPS: Stopped"
                fpsTextView.setTextColor(Color.GRAY)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping FPS monitoring", e)
        }
    }

    override fun doFrame(frameTimeNanos: Long) {
        try {
            if (!isMonitoring) return
            frameCount++
            frameTimes.add(frameTimeNanos)
            if (frameTimes.size > maxFrameHistory) frameTimes.removeAt(0)
            if (frameCount % 30 == 0 || frameTimeNanos - lastFrameTime >= 1_000_000_000L) {
                calculateFPS()
                updateFPSDisplay()
                lastFrameTime = frameTimeNanos
            }
            if (isMonitoring) choreographer.postFrameCallback(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error in doFrame", e)
            handler.post {
                fpsTextView.text = "FPS: Error"
                fpsTextView.setTextColor(Color.RED)
            }
        }
    }

    private fun calculateFPS() {
        try {
            if (frameTimes.size < 2) { fps = 0.0; return }
            val startTime = frameTimes.first()
            val endTime = frameTimes.last()
            val timeDifference = endTime - startTime
            fps = if (timeDifference > 0) {
                ((frameTimes.size - 1) * 1_000_000_000.0 / timeDifference).coerceIn(0.0, 240.0)
            } else 0.0
            if (frameCount % 60 == 0) Log.d(TAG, "FPS calculated: $fps")
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating FPS", e)
            fps = 0.0
        }
    }

    private fun updateFPSDisplay() {
        try {
            handler.post {
                val fpsText = "FPS: ${fpsFormat.format(fps)}"
                fpsTextView.text = fpsText
                val color = when {
                    fps >= 55 -> Color.GREEN
                    fps >= 30 -> Color.YELLOW
                    fps > 0 -> Color.RED
                    else -> Color.GRAY
                }
                fpsTextView.setTextColor(color)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating FPS display", e)
        }
    }
    
    override fun onDetachedFromWindow() {
        Log.d(TAG, "View detached from window")
        stopFPSMonitoring()
        super.onDetachedFromWindow()
    }
}
