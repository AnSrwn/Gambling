package com.example.user.gambling.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.user.gambling.R

class SettingsActivity : AppCompatActivity(){

    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.BetterOne)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.beginTransaction().add(R.id.settingsContainer, settingsFragment).commit()
    }
}
