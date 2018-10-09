package com.example.user.gambling.models

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ScoreViewModel : ViewModel() {
    val myScore = MutableLiveData<Int>()
    val opponentScore = MutableLiveData<Int>()

}
