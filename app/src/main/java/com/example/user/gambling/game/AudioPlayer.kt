package com.example.user.gambling.game

import android.content.Context
import android.media.MediaPlayer
import com.example.user.gambling.R


class AudioPlayer(val context: Context) {
    private lateinit var mp: MediaPlayer

    fun startDiceShakeSound() {
        mp = MediaPlayer.create (context, R.raw.sound_dice_shake)
        mp.isLooping = true
        mp.start()
    }

    fun stopDiceShakeSound() {
        mp.stop()
    }

    fun playDiceRollSound() {
        mp = MediaPlayer.create (context, R.raw.sound_dice_roll)
        mp.isLooping = false
        mp.start()
    }

}