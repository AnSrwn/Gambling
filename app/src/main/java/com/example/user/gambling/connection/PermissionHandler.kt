package com.example.user.gambling.connection

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat

/**
 * With this class you can request all permissions needed, to establish a connection with
 * Nearby Connections API.
 */
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

    /**
     * Method to check if the permissions are already granted.
     * @param permissions array of required permissions.
     * @return if one permission is not granted yet, it returns false, otherwise true.
     */
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