package com.example.user.gambling.database.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.example.user.gambling.database.entities.Score

@Dao
interface ScoreDao{
    @Query("SELECT * FROM score")
    fun getAll(): LiveData<List<Score>>

    @Query("SELECT * FROM score")
    fun getAllNotLive():List<Score>

    @Query("SELECT * FROM score ORDER BY score_entry ASC LIMIT 1")
    fun getEntryWithMinScore():Score

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(score: Score)

    @Update
    fun update(score: Score)
}