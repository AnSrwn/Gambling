package com.example.user.gambling.database.databases

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
import com.example.user.gambling.database.daos.ScoreDao
import com.example.user.gambling.database.entities.Score

@Database(entities = [(Score::class)], version = 1)
abstract class ScoreDB : RoomDatabase() {
    abstract fun scoreDB(): ScoreDao

    /* one and only one instance */
    companion object {
        private var sInstance: ScoreDB? = null

        fun getInstance(context: Context): ScoreDB? {
            Log.d("DBG", "Started get Instance")
            if (sInstance == null) {
                synchronized(ScoreDB::class) {
                    Log.d("DBG", "Synchronized Block")
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            ScoreDB::class.java, "score.db")
                            //.fallbackToDestructiveMigration()
                            .build()
                }
            }
            Log.d("DBG", "End of synch Block")
            return sInstance
        }
    }
}