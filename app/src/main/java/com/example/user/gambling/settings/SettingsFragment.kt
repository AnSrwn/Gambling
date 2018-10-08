package com.example.user.gambling.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.example.user.gambling.R

class SettingsFragment : PreferenceFragmentCompat(){

    //TODO show choice beneath category

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)
        //preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this) // register listener
        //setPrefTextInformation()
    }
    /*

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        updateChangedPreference(key)
        Log.d("DBG", "Preference Changed")
    }

    private fun setPrefTextInformation(){
        val prefs : Map<String, *> = PreferenceManager.getDefaultSharedPreferences(requireContext()).all
        val mapIterator = prefs.iterator()
        while(mapIterator.hasNext()){
            val nextKey = mapIterator.next().key
            Log.d("DBG", nextKey)
            val pref : Preference = findPreference(nextKey)
            pref.summary = prefs.getValue(nextKey).toString()
        }
    }

    private fun updateChangedPreference(key: String?){
        if(key != null) {
            val changedPref: Preference = findPreference(key)
            changedPref.summary = PreferenceManager.getDefaultSharedPreferences(requireActivity())?.getString(key, "")
        }
    } */
}