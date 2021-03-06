package com.example.user.gambling.models

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.user.gambling.database.databases.ScoreDB
import com.example.user.gambling.database.entities.Score

/**
 * View Model over new rolled valid scores which are inserted in the DB.
 */
class DBScoreViewModel (application: Application): AndroidViewModel(application){
    private val scores: LiveData<List<Score>> = ScoreDB.getInstance(getApplication())!!.scoreDB().getAll()
    fun getScores() = scores
}