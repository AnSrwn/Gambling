package com.example.user.gambling.game

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.user.gambling.R
import com.example.user.gambling.database.databases.ScoreDB
import com.example.user.gambling.database.entities.Score
import com.example.user.gambling.game.score.DiceScore
import com.example.user.gambling.game.score.ScoreViewModel
import kotlinx.android.synthetic.main.fragment_dice_singleplayer.*
import org.jetbrains.anko.doAsync
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import android.widget.Toast

class DiceGameFragment : android.support.v4.app.Fragment() {

    private var shakeListener: ShakeListener? = null
    private var gifImageViewDiceCup: GifImageView? = null
    private var gifDrawable: GifDrawable? = null
    private var imageViewDice1: ImageView? = null
    private var imageViewDice2: ImageView? = null
    private var btnRestart: Button? = null

    private val diceScore = DiceScore(2)

    private var isMultiplayer = false

    private var currentPlayerName : String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        isMultiplayer = arguments!!.getBoolean("isMultiplayer")
        currentPlayerName = arguments!!.getString("playerName")

        val view = inflater.inflate(R.layout.fragment_dice_singleplayer, container, false)

        imageViewDice1 = view.findViewById<View>(R.id.imageViewFirstDice) as ImageView
        imageViewDice1!!.setImageResource(R.drawable.dice1)

        imageViewDice2 = view.findViewById<View>(R.id.imageViewSecondDice) as ImageView
        imageViewDice2!!.setImageResource(R.drawable.dice1)

        gifImageViewDiceCup = view.findViewById<View>(R.id.gifRollingDices) as GifImageView
        gifImageViewDiceCup!!.setImageResource(R.drawable.gif_set_cup)
        gifDrawable = gifImageViewDiceCup!!.drawable as GifDrawable

        btnRestart = view.findViewById(R.id.buttonRestart)
        btnRestart!!.setOnClickListener {
            restartGameFragment()
        }
        btnRestart!!.visibility = View.GONE

        shakeListener = ShakeListener(activity!!.applicationContext)
        startGifAnimations()

        return view
    }

    override fun onResume() {
        shakeListener!!.resume()
        super.onResume()
    }

    override fun onPause() {
        shakeListener!!.pause()
        super.onPause()
    }

    private fun startGifAnimations() {
        Handler().postDelayed({
            setDicesVisibilty(false)
        }, 400)

        Handler().postDelayed({
            Toast.makeText(
                    activity,
                    getString(R.string.dice_single_toast_start_shaking),
                    Toast.LENGTH_SHORT).show()

            startShakeListener()
        }, 800)
    }

    private fun startShakeListener() {
        shakeListener?.setOnShakeListener(object : ShakeListener.OnShakeListener {

            var pickupAnimationFinished = false
            var setAnimationRunning = false
            var shakingAnimationFinished = false
            var shakingAnimationRunning = false

            override fun onShake() {
                if(!shakingAnimationRunning) {
                    gifImageViewDiceCup!!.setImageResource(R.drawable.gif_real_cup)
                    gifDrawable!!.start()
                    shakingAnimationRunning = true

                    Handler().postDelayed({
                        shakingAnimationRunning = false
                        shakingAnimationFinished = true
                    }, 1000)
                }
            }

            override fun onShakeStop() {
                if(!pickupAnimationFinished && shakingAnimationFinished) {
                    pickupAnimationFinished = true

                    diceScore.generateNewScores()
                    changeDice(imageViewDice1!!, diceScore.scoresOfDices[0])
                    changeDice(imageViewDice2!!, diceScore.scoresOfDices[1])
                    setDicesVisibilty(true)

                    gifImageViewDiceCup!!.setImageResource(R.drawable.gif_pickup_cup)
                    gifDrawable!!.start()

                    Handler().postDelayed({
                        if(isMultiplayer) {
                            activity?.let {
                                val scoreViewModel = ViewModelProviders.of(it).get(ScoreViewModel::class.java)
                                scoreViewModel.myScore.postValue(diceScore.sumOfScores)
                            }

                            val gameFragment = fragmentManager!!.findFragmentByTag("gameFragment")
                            fragmentManager!!.beginTransaction().remove(gameFragment!!).commit()

                            val multiplayerFragment = fragmentManager!!.findFragmentByTag("multiplayerFragment")
                            fragmentManager!!.beginTransaction().show(multiplayerFragment!!).commit()
                        } else {
                            btnRestart!!.visibility = View.VISIBLE
                            textViewScore.text = getString(R.string.dice_single_score, diceScore.sumOfScores)
                            //Save score only for single player
                            insertScoreinDB(currentPlayerName!!, diceScore.sumOfScores)
                        }

                        gifDrawable!!.stop()
                        gifImageViewDiceCup!!.visibility = View.GONE

                        shakeListener?.pause()
                    }, 800)
                }
            }

            override fun onResume() {
                pickupAnimationFinished = false
                setAnimationRunning = false
                shakingAnimationFinished = false
                shakingAnimationRunning = false
            }
        })
    }

    private fun restartGameFragment() {
        val diceSingleplayerFragment = fragmentManager!!.findFragmentByTag("singleplayerFragment")

        fragmentManager!!.beginTransaction()
                .detach(diceSingleplayerFragment!!)
                .attach(diceSingleplayerFragment)
                .commit()
    }

    private fun setDicesVisibilty(setVisible: Boolean) {
        if(setVisible) {
            imageViewDice1!!.visibility = View.VISIBLE
            imageViewDice2!!.visibility = View.VISIBLE

        } else {
            imageViewDice1!!.visibility = View.GONE
            imageViewDice2!!.visibility = View.GONE
        }
    }

    private fun changeDice(view: ImageView?, score: Int) {
        when (score) {
            1 -> view!!.setImageResource(R.drawable.dice1)
            2 -> view!!.setImageResource(R.drawable.dice2)
            3 -> view!!.setImageResource(R.drawable.dice3)
            4 -> view!!.setImageResource(R.drawable.dice4)
            5 -> view!!.setImageResource(R.drawable.dice5)
            6 -> view!!.setImageResource(R.drawable.dice6)
        }
    }

    private fun insertScoreinDB(playerName : String, rolledScore: Int) {
        val db = ScoreDB.getInstance(context!!)!!.scoreDB()
        doAsync {
            val curSize = db.getAllNotLive().size
            db.insert(Score(curSize+1, playerName ,rolledScore))
            Log.d("DBG", "${rolledScore} in DB inserted")
        }
    }
}