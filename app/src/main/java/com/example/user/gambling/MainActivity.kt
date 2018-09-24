package com.example.user.gambling

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.example.user.gambling.game.DiceMenuFragment
import android.view.MenuItem
import com.example.user.gambling.settings.SettingsFragment


class MainActivity : AppCompatActivity() {

    private val diceMenuFragment = DiceMenuFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().addToBackStack(null).add(R.id.fragmentContainer, diceMenuFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.settings -> {
                val settingsFragment = SettingsFragment()
                supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, settingsFragment ).commit()
                true
            }
            R.id.help -> {
                //TODO no help implemented
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
