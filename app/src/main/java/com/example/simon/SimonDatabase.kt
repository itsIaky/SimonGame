package com.example.simon

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ScoreEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ScoreConverters::class)
abstract class SimonDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile
        private var instance: SimonDatabase? = null

        fun getInstance(context: Context): SimonDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = SimonDatabase::class.java,
                    name = "simon.db"
                ).build().also { createdDb ->
                    instance = createdDb
                }
            }
        }
    }
}
