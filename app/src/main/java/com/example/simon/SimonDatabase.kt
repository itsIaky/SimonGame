package com.example.simon

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// this class defines the room database used by the whole app.
@Database(
    // tables managed by this database.
    entities = [ScoreEntity::class],
    // current schema version.
    version = 1,
    // do not export schema files to the project folder.
    exportSchema = false
)
// use these converters to store unsupported types (for example List<Char>).
@TypeConverters(ScoreConverters::class)
abstract class SimonDatabase : RoomDatabase() {
    // gives access to score queries (read/write methods in ScoreDao).
    abstract fun scoreDao(): ScoreDao

    companion object {
        // shared database instance.
        // @Volatile keeps reads/writes visible across threads
        @Volatile
        private var instance: SimonDatabase? = null

        // returns the single app-wide database instance
        fun getInstance(context: Context): SimonDatabase {
            // if already created, return it
            // otherwise create it once in a thread-safe block
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    // use applicationContext to avoid leaking an Activity context
                    context = context.applicationContext,
                    // database class room must instantiate
                    klass = SimonDatabase::class.java,
                    // file name of the sqlite database on device
                    name = "simon.db"
                ).build().also { createdDb ->
                    // Save the created instance so future calls reuse it
                    instance = createdDb
                }
            }
        }
    }
}
