package com.example.simon

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScoreRepository(
    private val scoreDao: ScoreDao
) {
    val playedGames: Flow<List<Score>> = scoreDao.observePlayedGames()
        .map { entities -> entities.map { it.toDomain() } }

    fun observePlayedGameById(scoreId: Long): Flow<Score?> {
        return scoreDao.observePlayedGameById(scoreId)
            .map { entity -> entity?.toDomain() }
    }

    suspend fun saveScore(score: Score): Long {
        return scoreDao.insertScore(score.toEntity())
    }
}
