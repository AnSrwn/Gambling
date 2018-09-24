package com.example.user.gambling.settings

import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import com.example.user.gambling.R

class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val exercisesPref: Preference = findPreference("pref_user_key")
        exercisesPref.summary = PreferenceManager.getDefaultSharedPreferences(activity)?.getString("pref_user_key", "")
    }
}