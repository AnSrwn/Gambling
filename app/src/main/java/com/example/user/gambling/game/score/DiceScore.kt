package com.example.user.gambling.game.score

import java.util.*

/**
 * This class is used to generate dice scores.
 * @param numberOfDices defines how many scores should be generated.
 */
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

    /**
     * Generates a random number.
     */
    private fun IntRange.random() = Random().nextInt((endInclusive + 1) - start) +  start
}