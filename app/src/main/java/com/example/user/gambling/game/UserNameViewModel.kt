package com.example.user.gambling.game

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class UserNameViewModel : ViewModel(){
    val userName = MutableLiveData<String>()
}