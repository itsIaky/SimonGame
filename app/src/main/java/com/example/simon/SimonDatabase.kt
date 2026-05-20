package com.example.simon

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// this class defines the room database used by the whole app
@Database(
    // tables managed by this database
    entities = [ScoreEntity::class],
    // current schema version
    version = 2,
    // do not export schema files to the project folder
    exportSchema = false
)
// use these converters to store unsupported types (for example List<Char>)
@TypeConverters(ScoreConverters::class)
abstract class SimonDatabase : RoomDatabase() {
    // gives access to score queries (read/write methods in ScoreDao)
    abstract fun scoreDao(): ScoreDao

    companion object {
        // shared database instance
        // @Volatile keeps reads/writes visible across threads
        @Volatile
        private var instance: SimonDatabase? = null

        // Migration from schema v1 to v2:
        // old column name: playedGamesSequence
        // new column name: playedGameSequence
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `played_games_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `playedGameSequence` TEXT NOT NULL,
                        `playedUserSequence` TEXT NOT NULL,
                        `maxCorrectSequence` INTEGER NOT NULL,
                        `errorPosition` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `played_games_new` (
                        `id`,
                        `playedGameSequence`,
                        `playedUserSequence`,
                        `maxCorrectSequence`,
                        `errorPosition`
                    )
                    SELECT
                        `id`,
                        `playedGamesSequence`,
                        `playedUserSequence`,
                        `maxCorrectSequence`,
                        `errorPosition`
                    FROM `played_games`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `played_games`")
                db.execSQL("ALTER TABLE `played_games_new` RENAME TO `played_games`")
            }
        }

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
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { createdDb ->
                    // Save the created instance so future calls reuse it
                    instance = createdDb
                }
            }
        }
    }
}
