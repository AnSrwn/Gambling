package com.example.user.gambling.connection

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat

class PermissionHandler {

    private val requestCodeRequiredPermissions = 1

    private val requiredPermissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION)

    fun requestPermissions(activity: Activity): Boolean {
        if (!hasPermissions(activity.applicationContext, *requiredPermissions)) {
            requestPermissions(activity, requiredPermissions, requestCodeRequiredPermissions)

            if(!hasPermissions(activity.applicationContext, *requiredPermissions)) {
                return false
            }
        }
        return true
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}