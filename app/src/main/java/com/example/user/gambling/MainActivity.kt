package com.example.user.gambling

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.user.gambling.game.DiceMenuFragment

class MainActivity : AppCompatActivity() {

    private val diceMenuFragment = DiceMenuFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, diceMenuFragment).commit()
    }
}
