package com.fpsmeter.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private lateinit var permissionHelper: PermissionHelper
    private var isServiceRunning = false
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "FPSMeter_MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        
        try {
            setContentView(R.layout.activity_main)
            initViews()
            permissionHelper = PermissionHelper(this)
            checkOverlayPermission()
            Log.d(TAG, "onCreate completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            showError("Failed to initialize app: ${e.message}")
        }
    }

    private fun initViews() {
        try {
            statusText = findViewById(R.id.status_text)
            toggleButton = findViewById(R.id.toggle_button)
            
            toggleButton.setOnClickListener {
                Log.d(TAG, "Toggle button clicked, isServiceRunning: $isServiceRunning")
                if (isServiceRunning) {
                    stopFPSMeter()
                } else {
                    startFPSMeter()
                }
            }
            Log.d(TAG, "Views initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            showError("Failed to initialize interface")
        }
    }

    private fun checkOverlayPermission() {
        try {
            if (!permissionHelper.hasOverlayPermission()) {
                statusText.text = "Overlay permission required"
                toggleButton.text = "Grant Permission"
                toggleButton.setOnClickListener {
                    Log.d(TAG, "Requesting overlay permission")
                    permissionHelper.requestOverlayPermission()
                }
                Log.d(TAG, "Overlay permission not granted")
            } else {
                statusText.text = "Ready to start FPS monitoring"
                toggleButton.text = "Start FPS Meter"
                Log.d(TAG, "Overlay permission already granted")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking overlay permission", e)
            showError("Permission check failed")
        }
    }

    private fun startFPSMeter() {
        Log.d(TAG, "Starting FPS meter...")
        
        try {
            if (!permissionHelper.hasOverlayPermission()) {
                Log.w(TAG, "Overlay permission not available when starting")
                showError("Overlay permission required")
                checkOverlayPermission()
                return
            }

            statusText.text = "Starting FPS monitoring..."
            toggleButton.isEnabled = false
            
            val intent = Intent(this, FPSMeterService::class.java)
            
            try {
                startForegroundService(intent)
                Log.d(TAG, "Foreground service start requested")
                handler.postDelayed({ checkServiceStatus() }, 2000)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start foreground service", e)
                showError("Failed to start monitoring service")
                resetUI()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in startFPSMeter", e)
            showError("Failed to start FPS monitoring")
            resetUI()
        }
    }
    
    private fun checkServiceStatus() {
        if (isServiceRunning) {
            statusText.text = "FPS Meter is running"
            toggleButton.text = "Stop FPS Meter"
            toggleButton.isEnabled = true
            Log.d(TAG, "Service confirmed running")
        } else {
            Log.w(TAG, "Service may not have started properly")
            statusText.text = "Service may have failed to start. Check permissions and try again."
            toggleButton.text = "Retry Start FPS Meter"
            toggleButton.isEnabled = true
        }
    }

    private fun stopFPSMeter() {
        Log.d(TAG, "Stopping FPS meter...")
        
        try {
            statusText.text = "Stopping FPS monitoring..."
            toggleButton.isEnabled = false
            
            val intent = Intent(this, FPSMeterService::class.java)
            stopService(intent)
            
            handler.postDelayed({
                isServiceRunning = false
                statusText.text = "FPS Meter stopped"
                toggleButton.text = "Start FPS Meter"
                toggleButton.isEnabled = true
                Log.d(TAG, "Service stopped")
            }, 1000)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping FPS meter", e)
            showError("Failed to stop FPS monitoring")
            resetUI()
        }
    }
    
    private fun resetUI() {
        isServiceRunning = false
        statusText.text = "Ready to start FPS monitoring"
        toggleButton.text = "Start FPS Meter"
        toggleButton.isEnabled = true
    }

    private fun showError(message: String) {
        Log.e(TAG, "Showing error: $message")
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            statusText.text = message
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        
        try {
            if (permissionHelper.hasOverlayPermission() && !isServiceRunning) {
                statusText.text = "Ready to start FPS monitoring"
                toggleButton.text = "Start FPS Meter"
                toggleButton.isEnabled = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode=$requestCode")
        
        if (requestCode == PermissionHelper.OVERLAY_PERMISSION_REQUEST_CODE) {
            handler.postDelayed({ checkOverlayPermission() }, 500)
        }
    }
}
