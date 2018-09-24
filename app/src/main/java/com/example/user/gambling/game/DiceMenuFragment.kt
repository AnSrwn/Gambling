package com.example.user.gambling.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.user.gambling.R

class DiceMenuFragment : android.support.v4.app.Fragment() {

    private val diceSingleplayerFragment = DiceSingleplayerFragment()
    private val diceMultiplayerFragment = DiceMultiplayerFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_dice_menu, container, false)
        val buttonStartSingleplayerFragment = view.findViewById(R.id.buttonStartSingleplayerFragment) as Button
        val buttonStartMultiplayerFragment = view.findViewById(R.id.buttonStartMultiplayerFragment) as Button

        buttonStartSingleplayerFragment.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragmentContainer, diceSingleplayerFragment).addToBackStack(null).commit()
        }

        buttonStartMultiplayerFragment.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragmentContainer, diceMultiplayerFragment).addToBackStack(null).commit()
        }

        return view
    }
}
