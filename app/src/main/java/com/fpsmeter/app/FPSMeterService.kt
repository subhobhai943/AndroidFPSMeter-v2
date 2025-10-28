package com.fpsmeter.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import android.view.Gravity

class FPSMeterService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: FPSOverlayView? = null
    private var isOverlayVisible = false

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "fps_meter_channel"
        private const val TAG = "FPSMeter_Service"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate called")
        
        try {
            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            createNotificationChannel()
            Log.d(TAG, "Service onCreate completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in service onCreate", e)
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand called")
        
        try {
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)
            Log.d(TAG, "Foreground service started with notification")
            
            showOverlay()
            
            return START_STICKY
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStartCommand", e)
            showToast("Failed to start FPS monitoring: ${e.message}")
            stopSelf()
            return START_NOT_STICKY
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showOverlay() {
        Log.d(TAG, "Attempting to show overlay")
        
        if (isOverlayVisible) {
            Log.w(TAG, "Overlay already visible")
            return
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!android.provider.Settings.canDrawOverlays(this)) {
                    Log.error(TAG, "Overlay permission not granted")
                    showToast("Overlay permission not granted")
                    stopSelf()
                    return
                }
            }

            overlayView = FPSOverlayView(this)
            Log.d(TAG, "FPSOverlayView created")
            
            val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
            
            params.gravity = Gravity.TOP or Gravity.END
            params.x = 50
            params.y = 100
            
            try {
                windowManager.addView(overlayView, params)
                isOverlayVisible = true
                overlayView?.startFPSMonitoring()
                Log.d(TAG, "Overlay added to WindowManager successfully")
                showToast("FPS monitoring started")
            } catch (e: WindowManager.BadTokenException) {
                Log.e(TAG, "BadTokenException when adding overlay", e)
                showToast("Failed to show overlay: Invalid window token")
                stopSelf()
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException when adding overlay", e)
                showToast("Failed to show overlay: Permission denied")
                stopSelf()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected exception when adding overlay", e)
                showToast("Failed to show overlay: ${e.message}")
                stopSelf()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in showOverlay", e)
            showToast("Failed to create overlay")
            stopSelf()
        }
    }

    private fun hideOverlay() {
        Log.d(TAG, "Hiding overlay")
        
        if (!isOverlayVisible || overlayView == null) {
            Log.d(TAG, "Overlay not visible or null, nothing to hide")
            return
        }

        try {
            overlayView?.stopFPSMonitoring()
            windowManager.removeView(overlayView)
            isOverlayVisible = false
            overlayView = null
            Log.d(TAG, "Overlay removed successfully")
            showToast("FPS monitoring stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay", e)
            isOverlayVisible = false
            overlayView = null
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy called")
        super.onDestroy()
        hideOverlay()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "FPS Meter Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows FPS meter overlay"
                    setShowBadge(false)
                }
                
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created")
            } catch (e: Exception) {
                Log.e(TAG, "Error creating notification channel", e)
            }
        }
    }

    private fun createNotification(): Notification {
        return try {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FPS Meter")
                .setContentText("Monitoring FPS in real-time")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification", e)
            Notification.Builder(this)
                .setContentTitle("FPS Meter")
                .setContentText("Running")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        }
    }
    
    private fun showToast(message: String) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Toast shown: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing toast", e)
        }
    }
}
