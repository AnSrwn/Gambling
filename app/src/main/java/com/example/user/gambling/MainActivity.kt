package com.example.user.gambling

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.view.Menu
import com.example.user.gambling.game.MenuFragment
import android.view.MenuItem
import com.example.user.gambling.settings.SettingsActivity
import com.example.user.gambling.utility.Utils
import kotlinx.android.synthetic.main.activity_main.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.jerem.imagebackground.DrawableViewBackgroundTarget

class MainActivity : AppCompatActivity() {

    companion object {
        internal const val BACKGROUND_IMAGE_URI_KEY = "backgroundImageURI"
        private const val DEFAULT_DRAWABLE = R.drawable.wood
        internal val DEFAULT_DRAWABLE_URI = "android.resource://com.example.user.gambling/$DEFAULT_DRAWABLE"
    }

    private val diceMenuFragment = MenuFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.onActivityCreateSetTheme(this) // Load current theme set in Utils
        setContentView(R.layout.activity_main)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //TODO Landscape for tablets, but currently not needed

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
                val helpFragment = HelpFragment()
                supportFragmentManager!!.beginTransaction().replace(
                        R.id.fragmentContainer,
                        helpFragment,
                        "helpFragment").addToBackStack(null).commit()
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

    /**
     * Load other shared preferences than theme.
     */
    private fun loadPreferences(){
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        //val color = sp.getString("pref_bc_key", "white")
        val picturePath = sp.getString(BACKGROUND_IMAGE_URI_KEY, DEFAULT_DRAWABLE_URI)
        val viewTarget = DrawableViewBackgroundTarget(fragmentContainer)

        Glide.with(this)
                .load(picturePath)
                .apply(RequestOptions.centerCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(viewTarget) //TODO Extract and add transparency

        //fragmentContainer.setBackgroundColor(Color.parseColor(color))
        //TODO Some other preferences stuff
    }

    /**
     * Set old theme to the new one from shared preferences, then restart app.
     */
    private fun reloadTheme(){
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = sp.getString("pref_theme_key", Utils.THEME_LIGHT)
        Utils.changeToTheme(this, theme)
    }


}
