package com.example.user.gambling.game

import java.util.*

class DiceScore(numberOfDices: Int) {

    var scoresOfDices: MutableList<Int> = arrayListOf()
    val size = numberOfDices
    var sumOfScores = 0

    fun generateNewScores() {
        sumOfScores = 0
        scoresOfDices.clear()
        for (i in 1..size) {
            scoresOfDices.add((1..6).random())
            sumOfScores += scoresOfDices[i-1]
        }
    }

    private fun IntRange.random() = Random().nextInt((endInclusive + 1) - start) +  start
}