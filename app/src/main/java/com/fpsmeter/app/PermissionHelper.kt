package com.fpsmeter.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

class PermissionHelper(private val activity: Activity) {
    
    companion object {
        const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    }
    
    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity)
        } else {
            true // Permission not required for older versions
        }
    }
    
    fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity.packageName}")
                )
                activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }
    }
}