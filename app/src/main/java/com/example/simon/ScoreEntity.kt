package com.example.simon

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "played_games")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val playedGameSequence: List<Char>,
    val playedUserSequence: List<Char>,
    val maxCorrectSequence: Int,
    val errorPosition: Int
)

fun ScoreEntity.toDomain(): Score = Score(
    id = id,
    playedGameSequence = playedGameSequence,
    playedUserSequence = playedUserSequence,
    maxCorrectSequence = maxCorrectSequence,
    errorPosition = errorPosition
)

fun Score.toEntity(): ScoreEntity = ScoreEntity(
    id = id,
    playedGameSequence = playedGameSequence,
    playedUserSequence = playedUserSequence,
    maxCorrectSequence = maxCorrectSequence,
    errorPosition = errorPosition
)
