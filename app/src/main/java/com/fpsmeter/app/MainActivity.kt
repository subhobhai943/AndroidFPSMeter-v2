package com.fpsmeter.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private lateinit var permissionHelper: PermissionHelper
    private var isServiceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        permissionHelper = PermissionHelper(this)
        checkOverlayPermission()
    }

    private fun initViews() {
        statusText = findViewById(R.id.status_text)
        toggleButton = findViewById(R.id.toggle_button)
        
        toggleButton.setOnClickListener {
            if (isServiceRunning) {
                stopFPSMeter()
            } else {
                startFPSMeter()
            }
        }
    }

    private fun checkOverlayPermission() {
        if (!permissionHelper.hasOverlayPermission()) {
            statusText.text = "Overlay permission required"
            toggleButton.text = "Grant Permission"
            toggleButton.setOnClickListener {
                permissionHelper.requestOverlayPermission()
            }
        } else {
            statusText.text = "Ready to start FPS monitoring"
            toggleButton.text = "Start FPS Meter"
        }
    }

    private fun startFPSMeter() {
        if (permissionHelper.hasOverlayPermission()) {
            val intent = Intent(this, FPSMeterService::class.java)
            startForegroundService(intent)
            isServiceRunning = true
            statusText.text = "FPS Meter is running"
            toggleButton.text = "Stop FPS Meter"
        } else {
            Toast.makeText(this, "Overlay permission required", Toast.LENGTH_SHORT).show()
            checkOverlayPermission()
        }
    }

    private fun stopFPSMeter() {
        val intent = Intent(this, FPSMeterService::class.java)
        stopService(intent)
        isServiceRunning = false
        statusText.text = "FPS Meter stopped"
        toggleButton.text = "Start FPS Meter"
    }

    override fun onResume() {
        super.onResume()
        if (permissionHelper.hasOverlayPermission() && !isServiceRunning) {
            statusText.text = "Ready to start FPS monitoring"
            toggleButton.text = "Start FPS Meter"
        }
    }
}