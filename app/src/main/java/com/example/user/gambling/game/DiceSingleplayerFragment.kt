package com.example.user.gambling.game

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.user.gambling.R
import kotlinx.android.synthetic.main.fragment_dice_singleplayer.*
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class DiceSingleplayerFragment : android.support.v4.app.Fragment() {

    private var shakeListener: ShakeListener? = null
    private var gifDrawable: GifDrawable? = null
    private var gifImageViewDiceCup: GifImageView? = null
    private var imageViewDice1: ImageView? = null
    private var imageViewDice2: ImageView? = null
    private val diceScore = DiceScore(2)
    private var isShaked = false

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
            override fun onShake() {
                setVisibilty(true)
                gifDrawable!!.start()

                isShaked = true
            }

            override fun onShakeStop() {
                if(isShaked) {
                    diceScore.generateNewScores()
                    changeDice(imageViewDice1!!, diceScore.scoresOfDices[0])
                    changeDice(imageViewDice2!!, diceScore.scoresOfDices[1])

                    shakeListener?.pause()
                    textViewScore.text = getString(R.string.dice_single_score, diceScore.sumOfScores)
                }

                setVisibilty(false)
                gifDrawable!!.stop()
            }
        })
        return view
    }

    private fun restartGame() {
        isShaked = false
        shakeListener!!.resume()
    }

    fun setVisibilty(isOnShake: Boolean) {
        if(isOnShake) {
            imageViewDice1!!.visibility = View.GONE
            imageViewDice2!!.visibility = View.GONE
            gifImageViewDiceCup!!.visibility = View.VISIBLE
        } else {
            imageViewDice1!!.visibility = View.VISIBLE
            imageViewDice2!!.visibility = View.VISIBLE
            gifImageViewDiceCup!!.visibility = View.GONE
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

    override fun onResume() {
        restartGame()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onResume()
    }

    override fun onPause() {
        shakeListener!!.pause()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        super.onPause()
    }
}
