package com.example.user.gambling.game

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.user.gambling.R
import com.example.user.gambling.database.databases.ScoreDB
import com.example.user.gambling.database.entities.Score
import com.example.user.gambling.game.score.DiceScoreListFragment
import com.example.user.gambling.game.score.DiceScoreListViewAdapter
import org.jetbrains.anko.doAsync

class DiceMenuFragment : android.support.v4.app.Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_dice_menu, container, false)
        val buttonStartSingleplayerFragment =
                view.findViewById(R.id.buttonStartSingleplayerFragment) as Button
        val buttonStartMultiplayerFragment =
                view.findViewById(R.id.buttonStartMultiplayerFragment) as Button
        val buttonShowScore =
                view.findViewById(R.id.buttonShowScore) as Button
        val buttonSetPlayerName =
                view.findViewById(R.id.buttonChangePlayersName) as Button


        val activityContext = activity!!.applicationContext
        val db = ScoreDB.getInstance(activityContext)!!.scoreDB()
        var test: List<Score>? = null
        doAsync {
            db.insert(Score(1, "Test", 20))
            db.insert(Score(2, "Test2", 10))
            Log.d("DBG", "Score inserted")
            test = db.getAllNotLive()
            Log.d("DBG", "Got all not Live ${db.getAllNotLive()}")

        }

        buttonSetPlayerName.setOnClickListener{
            Log.d("DBG", "Start Dialog")
            val dialogFragment = SetPlayerNameDialogFragment()
            dialogFragment.show(fragmentManager,"")
        }

        buttonStartSingleplayerFragment.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isMultiplayer", false)
            val diceSingleplayerFragment = DiceGameFragment()
            diceSingleplayerFragment.arguments = bundle

            fragmentManager!!.beginTransaction().replace(
                    R.id.fragmentContainer,
                    diceSingleplayerFragment).addToBackStack(null).commit()
        }

        buttonStartMultiplayerFragment.setOnClickListener {
            val diceMultiplayerFragment = DiceMultiplayerFragment()
            fragmentManager!!.beginTransaction().replace(
                    R.id.fragmentContainer,
                    diceMultiplayerFragment,
                    "multiplayerFragment").addToBackStack(null).commit()
        }

        buttonShowScore.setOnClickListener {
            val diceScoreListFragment = DiceScoreListFragment()
            diceScoreListFragment.listAdapter = DiceScoreListViewAdapter(activityContext, test)
            fragmentManager!!.beginTransaction().replace(
                    R.id.fragmentContainer,
                    diceScoreListFragment).addToBackStack(null).commit()
        }

        return view
    }
}
