package com.example.user.gambling.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.gambling.R
import android.widget.Toast

class DiceSingleplayerFragment : android.support.v4.app.Fragment() {

    private var mShaker: ShakeListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mShaker = ShakeListener(activity!!.applicationContext)
        mShaker?.setOnShakeListener(object : ShakeListener.OnShakeListener {
            override fun onShake() {

                //this will be done, when a shake is detected
                Toast.makeText(activity!!.applicationContext, "Shaked",
                        Toast.LENGTH_SHORT).show()
            }
        })

        return inflater.inflate(R.layout.fragment_dice_singleplayer, container, false)
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
