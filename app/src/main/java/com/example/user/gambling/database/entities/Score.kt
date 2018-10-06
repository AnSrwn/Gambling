package com.example.user.gambling.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Score(@PrimaryKey (autoGenerate = true) val id: Int,
                 @ColumnInfo(name = "player_name") val playerName: String,
                 @ColumnInfo(name = "score") val score : Int) {
}