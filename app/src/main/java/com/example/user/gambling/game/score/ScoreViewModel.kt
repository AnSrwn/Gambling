package com.example.user.gambling.game.score

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ScoreViewModel : ViewModel() {
    val myScore = MutableLiveData<Int>()
    val opponentScore = MutableLiveData<Int>()

}
