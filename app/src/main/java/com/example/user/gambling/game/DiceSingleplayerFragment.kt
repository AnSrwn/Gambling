package com.example.user.gambling.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.user.gambling.R
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class DiceSingleplayerFragment : android.support.v4.app.Fragment() {

    private var mShaker: ShakeListener? = null
    private var gifDrawable: GifDrawable? = null
    private var gifImageViewDiceCup: GifImageView? = null
    private var imageViewDice1: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_dice_singleplayer, container, false)

        imageViewDice1 = view.findViewById<View>(R.id.imageViewFirstDice) as ImageView
        imageViewDice1!!.setImageResource(R.drawable.dice1)

        gifImageViewDiceCup = view.findViewById<View>(R.id.gifRollingDices) as GifImageView
        gifImageViewDiceCup!!.setImageResource(R.drawable.gif_real_cup)
        gifDrawable = gifImageViewDiceCup!!.drawable as GifDrawable

        gifImageViewDiceCup!!.visibility = View.GONE
        gifDrawable!!.stop()

        mShaker = ShakeListener(activity!!.applicationContext)
        mShaker?.setOnShakeListener(object : ShakeListener.OnShakeListener {
            override fun onShake() {
                //this will be executed, when a shake is detected
                imageViewDice1!!.visibility = View.GONE
                gifImageViewDiceCup!!.visibility = View.VISIBLE
                gifDrawable!!.start()
            }

            override fun onShakeStop() {
                //this will be executed, when there is no shake detected
                imageViewDice1!!.visibility = View.VISIBLE
                gifImageViewDiceCup!!.visibility = View.GONE
                gifDrawable!!.stop()
            }
        })

        return view
    }

    override fun onResume() {
        mShaker!!.resume()
        super.onResume()
    }

    override fun onPause() {
        mShaker!!.pause()
        super.onPause()
    }
}
