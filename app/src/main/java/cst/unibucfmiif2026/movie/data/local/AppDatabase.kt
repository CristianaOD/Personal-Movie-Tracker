package cst.unibucfmiif2026.movie.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MovieEntity::class, WatchedMovieEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun watchedMovieDao(): WatchedMovieDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS watched_movies (
                        movieId INTEGER PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        overview TEXT NOT NULL,
                        posterPath TEXT,
                        backdropPath TEXT,
                        voteAverage REAL NOT NULL,
                        voteCount INTEGER NOT NULL,
                        genresSerialized TEXT NOT NULL,
                        rating INTEGER NOT NULL,
                        comment TEXT NOT NULL,
                        isFavorite INTEGER NOT NULL,
                        watchedAt INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personal_movie_tracker.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
        }
    }
}