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
        setSettingInformation()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this) // register listener
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d("DBG", "Preference Changed")
        setSettingInformation()
    }

    private fun setSettingInformation(){
        val prefs : Map<String, *> = PreferenceManager.getDefaultSharedPreferences(activity).all
        val mapIterator = prefs.iterator()
        while(mapIterator.hasNext()){
            val nextKey = mapIterator.next().key
            val pref : Preference = findPreference(nextKey)
            pref.summary = PreferenceManager.getDefaultSharedPreferences(activity)?.getString(nextKey, "")
        }
    }
}