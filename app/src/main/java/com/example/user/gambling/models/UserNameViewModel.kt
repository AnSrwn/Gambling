package com.example.user.gambling.models

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * ViewModel over the users player name.
 */
class UserNameViewModel : ViewModel(){
    val userName = MutableLiveData<String>()
}