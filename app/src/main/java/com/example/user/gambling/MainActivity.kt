package com.example.user.gambling

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.user.gambling.game.DiceSingleplayerFragment

class MainActivity : AppCompatActivity() {

    val diceSingleplayerFragment = DiceSingleplayerFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
