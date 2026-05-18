package com.example.simon

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// marks this interface as a room DAO (Data Access Object)
// room generates implementation of this interface at compile time
@Dao
interface ScoreDao {
    // stream the played games list, newest first
    @Query("SELECT * FROM played_games ORDER BY id DESC")
    fun observePlayedGames(): Flow<List<ScoreEntity>>

    // stream one played game by database id
    @Query("SELECT * FROM played_games WHERE id = :scoreId LIMIT 1")
    fun observePlayedGameById(scoreId: Long): Flow<ScoreEntity?>

    // insert a new row and return the generated primary key
    // suspend means it runs in coroutines (non-blocking)
    @Insert
    suspend fun insertScore(score: ScoreEntity): Long
}
