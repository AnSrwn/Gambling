package com.example.user.gambling.settings

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
import com.example.user.gambling.R

class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val dialogPreference = preferenceScreen.findPreference("pref_bc_pic_key") as Preference
        dialogPreference.setOnPreferenceClickListener {
            Log.d("DBG", "Start Dialog")
            val dialogFragment = SetBackgroundImageDialogFragment()
            dialogFragment.show(fragmentManager,"setBackground")
            true
        }
        //preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this) // register listener
        //setPrefTextInformation()
    }

    /*
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        updateChangedPreference(key)
        Log.d("DBG", "Preference Changed")
    }*/
}