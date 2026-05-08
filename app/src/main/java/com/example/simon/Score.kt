package com.example.simon

class Score(
    private val playedGamesSequence: List<Char>,
    private val maxCorrectSequence: Int,
    private val errorPosition: Int) {

    fun getPlayedGamesSequence(): List<Char> {
        return playedGamesSequence
    }

    fun getMaxCorrectSequence(): Int {
        return maxCorrectSequence
    }

    fun getErrorPosition(): Int {
        return errorPosition
    }
}