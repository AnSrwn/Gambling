package com.example.user.gambling.game

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.user.gambling.R
import com.example.user.gambling.game.score.DiceScore
import kotlinx.android.synthetic.main.fragment_dice_singleplayer.*
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class DiceSingleplayerFragment : android.support.v4.app.Fragment() {

    private var shakeListener: ShakeListener? = null
    private var gifImageViewDiceCup: GifImageView? = null
    private var gifDrawable: GifDrawable? = null
    private var imageViewDice1: ImageView? = null
    private var imageViewDice2: ImageView? = null

    private val diceScore = DiceScore(2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_dice_singleplayer, container, false)

        imageViewDice1 = view.findViewById<View>(R.id.imageViewFirstDice) as ImageView
        imageViewDice1!!.setImageResource(R.drawable.dice1)

        imageViewDice2 = view.findViewById<View>(R.id.imageViewSecondDice) as ImageView
        imageViewDice2!!.setImageResource(R.drawable.dice1)

        gifImageViewDiceCup = view.findViewById<View>(R.id.gifRollingDices) as GifImageView
        gifImageViewDiceCup!!.setImageResource(R.drawable.gif_real_cup)
        gifDrawable = gifImageViewDiceCup!!.drawable as GifDrawable

        gifImageViewDiceCup!!.visibility = View.GONE
        gifDrawable!!.stop()

        shakeListener = ShakeListener(activity!!.applicationContext)
        shakeListener?.setOnShakeListener(object : ShakeListener.OnShakeListener {

            var isShaked = false
            var pickupAnimationRunning = false
            var setAnimationRunning = false
            var shakingAnimationRunning = false


            override fun onShake() {

                //TODO find a way, to time animations without handlers
                //TODO improve and reduce the boolean animation variables

                if(!isShaked && !setAnimationRunning) {
                    gifImageViewDiceCup!!.setImageResource(R.drawable.gif_set_cup)
                    gifImageViewDiceCup!!.visibility = View.VISIBLE
                    gifDrawable!!.start()

                    Handler().postDelayed({
                        setDicesVisibilty(false)
                    }, 400)

                    setAnimationRunning = true
                } else if (!shakingAnimationRunning && setAnimationRunning) {
                    shakingAnimationRunning = true
                    isShaked = true

                    Handler().postDelayed({
                        gifDrawable!!.stop()
                        gifImageViewDiceCup!!.setImageResource(R.drawable.gif_real_cup)
                        gifDrawable!!.start()
                        shakingAnimationRunning = false
                    }, 1000)
                }
            }

            override fun onShakeStop() {
                if(isShaked && !pickupAnimationRunning && !shakingAnimationRunning) {
                    setAnimationRunning = false
                    pickupAnimationRunning = true

                    gifDrawable!!.stop()

                    diceScore.generateNewScores()
                    changeDice(imageViewDice1!!, diceScore.scoresOfDices[0])
                    changeDice(imageViewDice2!!, diceScore.scoresOfDices[1])
                    setDicesVisibilty(true)

                    gifImageViewDiceCup!!.setImageResource(R.drawable.gif_pickup_cup)
                    gifDrawable!!.start()

                    Handler().postDelayed({
                        textViewScore.text = getString(R.string.dice_single_score, diceScore.sumOfScores)

                        gifDrawable!!.stop()
                        gifImageViewDiceCup!!.visibility = View.GONE

                        shakeListener?.pause()
                    }, 800)
                }
            }

            override fun onResume() {
                isShaked = false
                pickupAnimationRunning = false
                setAnimationRunning = false
                shakingAnimationRunning = false
            }
        })
        return view
    }

    override fun onResume() {
        shakeListener!!.resume()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onResume()
    }

    override fun onPause() {
        shakeListener!!.pause()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        super.onPause()
    }

    fun setDicesVisibilty(setVisible: Boolean) {
        if(setVisible) {
            imageViewDice1!!.visibility = View.VISIBLE
            imageViewDice2!!.visibility = View.VISIBLE

        } else {
            imageViewDice1!!.visibility = View.GONE
            imageViewDice2!!.visibility = View.GONE
        }
    }

    fun changeDice(view: ImageView?, score: Int) {
        when (score) {
            1 -> view!!.setImageResource(R.drawable.dice1)
            2 -> view!!.setImageResource(R.drawable.dice2)
            3 -> view!!.setImageResource(R.drawable.dice3)
            4 -> view!!.setImageResource(R.drawable.dice4)
            5 -> view!!.setImageResource(R.drawable.dice5)
            6 -> view!!.setImageResource(R.drawable.dice6)
        }
    }
}