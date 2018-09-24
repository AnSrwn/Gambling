package com.example.user.gambling.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.example.user.gambling.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener{

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val exercisesPref: Preference = findPreference("pref_bc_key")
        exercisesPref.summary = PreferenceManager.getDefaultSharedPreferences(activity)?.getString("pref_bc_key", "")
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this) // register listener
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d("DBG", "Preference Changed")

        // Set summary to be the user-description for the selected value
        val exercisesPref: Preference = findPreference(key)
        exercisesPref.summary = sharedPreferences?.getString(key, "")
    }
}