package com.example.simon

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// stream => a value that can updates over time
// domain model => the app-level data shape used by your business/UI logic
// in this case Score is the domain model
// ScoreEntity is the room/database model

// this is a layer between view models and room
// repository converts DB model -> domain model
class ScoreRepository(
    private val scoreDao: ScoreDao
) {
    // stream all played games from DB and convert each entity to domain model
    val playedGames: Flow<List<Score>> = scoreDao.observePlayedGames()
        .map { entities -> entities.map { it.toDomain() } }

    // stream one played game by id and convert it to domain model if present.
    fun observePlayedGameById(scoreId: Long): Flow<Score?> {
        return scoreDao.observePlayedGameById(scoreId)
            .map { entity -> entity?.toDomain() }
    }

    // save a played game by converting domain model to entity.
    // returns the generated database id.
    suspend fun saveScore(score: Score): Long {
        return scoreDao.insertScore(score.toEntity())
    }
}
