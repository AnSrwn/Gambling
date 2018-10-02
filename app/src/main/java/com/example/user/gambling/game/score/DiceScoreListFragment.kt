package com.example.user.gambling.game.score

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.gambling.R

class DiceScoreListFragment : ListFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return  inflater.inflate(R.layout.fragment_dice_score_list, container, false);
    }
}