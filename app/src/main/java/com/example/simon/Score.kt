package com.example.simon

data class Score(
    val id: Long = 0L,
    val playedGameSequence: List<Char>,
    val playedUserSequence: List<Char>,
    val maxCorrectSequence: Int,
    val errorPosition: Int
)
