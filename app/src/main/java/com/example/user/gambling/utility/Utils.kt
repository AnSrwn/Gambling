package com.example.user.gambling.utility

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.user.gambling.R
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.net.Uri


/**
 * Utility class.
 */
class Utils {
    companion object {
        const val THEME_LIGHT = "light"
        private const val THEME_BLUE = "blue"
        private const val THEME_DARK = "dark"

        /**
         * Theme the activity accessses via onActivityCreateSetTheme(activity)
         */
        private var sTheme: String? = THEME_LIGHT


        /**
         * Change theme of an activity. Activity is restarted in this call and only if the new theme is different to the old one.
         * @param activity to restart with the new theme.
         * @param theme to change to.
         */
        fun changeToTheme(activity: Activity, theme: String?) {
            val oldTheme = sTheme
            if (theme != null) {
                sTheme = theme
            }
            if (theme != oldTheme) {
                activity.finish()
                activity.startActivity(Intent(activity, activity.javaClass))
                Log.d("DBG", "Theme changed")
            } else {
                Log.d("DBG", "Theme not changed")
            }
        }

        /**
         * Sets theme based on current theme set or the default.
         * Can only be called before a view exists or error.
         * @param activity to set theme on.
         */
        fun onActivityCreateSetTheme(activity: Activity) {
            when (sTheme) {
                THEME_BLUE -> activity.setTheme(R.style.AppTheme)
                THEME_DARK -> activity.setTheme(R.style.Dark)
                THEME_LIGHT -> activity.setTheme(R.style.Light)
                else -> activity.setTheme(R.style.Light)
            }
        }
    }
}