package com.example.user.gambling.game

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.user.gambling.R
import com.example.user.gambling.game.score.DiceScoreListFragment
import com.example.user.gambling.models.UserNameViewModel
import kotlinx.android.synthetic.main.fragment_dice_menu.*

class MenuFragment : android.support.v4.app.Fragment() {

    private var prefUsername: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_dice_menu, container, false)

        prefUsername = PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)

        val usernameText = view.findViewById(R.id.playerName) as TextView
        usernameText.text = prefUsername!!.getString(
                "username",
                getString(R.string.dice_menu_player_name))

        registerForPlayerNameUpdates()

        val buttonStartSingleplayerFragment =
                view.findViewById(R.id.buttonStartSingleplayerFragment) as Button
        val buttonStartMultiplayerFragment =
                view.findViewById(R.id.buttonStartMultiplayerFragment) as Button
        val buttonShowScore =
                view.findViewById(R.id.buttonShowScore) as Button
        val buttonSetPlayerName =
                view.findViewById(R.id.buttonChangePlayersName) as ImageButton



        buttonSetPlayerName.setOnClickListener{
            Log.d("DBG", "Start Dialog")
            val dialogFragment = SetPlayerNameDialogFragment()
            dialogFragment.show(fragmentManager,"playername")
        }

        buttonStartSingleplayerFragment.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isMultiplayer", false)
            bundle.putString("playerName", playerName.text.toString())
            val diceSingleplayerFragment = GameFragment()
            diceSingleplayerFragment.arguments = bundle

            fragmentManager!!.beginTransaction().replace(
                    R.id.fragmentContainer,
                    diceSingleplayerFragment,
                    "singleplayerFragment").addToBackStack(null).commit()
        }

        buttonStartMultiplayerFragment.setOnClickListener {
            val diceMultiplayerFragment = MultiplayerFragment()
            fragmentManager!!.beginTransaction().replace(
                    R.id.fragmentContainer,
                    diceMultiplayerFragment,
                    "multiplayerFragment").addToBackStack(null).commit()
        }

        buttonShowScore.setOnClickListener {
            val diceScoreListFragment = DiceScoreListFragment()
            fragmentManager!!.beginTransaction().replace(R.id.fragmentContainer, diceScoreListFragment)
                    .addToBackStack(null).commit()
        }
        return view
    }

    private fun registerForPlayerNameUpdates() {
        activity?.let { fragmentActivity ->
            val sharedViewModel = ViewModelProviders.of(fragmentActivity).get(UserNameViewModel::class.java)
            sharedViewModel.userName.observe(this, Observer { i ->
                i?.let {
                    playerName.text = it
                    prefUsername!!.edit().putString("username", it).apply()
                }
            })
        }
    }
}
