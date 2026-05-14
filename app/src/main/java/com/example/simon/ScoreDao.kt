package com.example.simon

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM played_games ORDER BY id DESC")
    fun observePlayedGames(): Flow<List<ScoreEntity>>

    @Query("SELECT * FROM played_games WHERE id = :scoreId LIMIT 1")
    fun observePlayedGameById(scoreId: Long): Flow<ScoreEntity?>

    @Insert
    suspend fun insertScore(score: ScoreEntity): Long
}
