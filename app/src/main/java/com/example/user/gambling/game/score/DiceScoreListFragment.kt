package com.example.user.gambling.game.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.gambling.R
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.widget.ListView
import com.example.user.gambling.models.DBScoreViewModel


class DiceScoreListFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View? = inflater.inflate(R.layout.fragment_dice_score_list, container, false)
        val listView = view!!.findViewById<View>(android.R.id.list) as ListView
        val dbScoreViewModel = ViewModelProviders.of(this).get(DBScoreViewModel::class.java)

        dbScoreViewModel.getScores().observe(this, Observer { scoreList ->
            listView.adapter = DiceScoreListViewAdapter(this.context!!,
                    scoreList?.sortedByDescending { it -> it.score  })
        })

        return view
    }
}