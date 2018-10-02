package com.example.user.gambling.game.score

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.user.gambling.R
import com.example.user.gambling.database.entities.Score

class DiceScoreListViewAdapter(private val context: Context, private val scores: List<Score>?):BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //TODO Maybe replace it with View Holder, whatever?
       /* val holder = RecyclerView.ViewHolder(
                convertView.findViewById(R.id.playerName),
                convertView.findViewById(R.id.playersScore)
        )
        convertView.tag = holder*/
        val rowView = inflater.inflate(R.layout.item_dice_score, parent, false)
        val thisScore = scores!![position]
        var tv = rowView.findViewById(R.id.playerName) as TextView
        tv.text = thisScore.playerName
        tv = rowView.findViewById(R.id.playersScore) as TextView
        tv.text = String.format("%d", thisScore.score)
        return rowView
    }

    override fun getItem(position: Int): Any {
        return scores!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return scores!!.size
    }
}