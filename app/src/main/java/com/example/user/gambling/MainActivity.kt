package com.example.user.gambling

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import com.example.user.gambling.game.DiceMenuFragment
import android.view.MenuItem
import com.example.user.gambling.database.databases.ScoreDB
import com.example.user.gambling.database.entities.Score
import com.example.user.gambling.settings.SettingsActivity
import com.example.user.gambling.utility.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync


class MainActivity : AppCompatActivity() {

    private val diceMenuFragment = DiceMenuFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.onActivityCreateSetTheme(this)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, diceMenuFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.help -> {
                //TODO no help implemented
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRestart() {
        super.onRestart()
        reloadTheme()
    }

    override fun onStart() {
        super.onStart()
        loadPreferences()
    }

    private fun loadPreferences(){
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val color = sp.getString("pref_bc_key", "white")
        fragmentContainer.setBackgroundColor(Color.parseColor(color))
        //TODO Some other preferences stuff
    }

    private fun reloadTheme(){
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = sp.getString("pref_theme_key", Utils.THEME_LIGHT)
        Utils.changeToTheme(this, theme)
    }
}
