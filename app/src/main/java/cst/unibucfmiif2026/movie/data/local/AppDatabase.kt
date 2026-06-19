package cst.unibucfmiif2026.movie.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MovieEntity::class, WatchedMovieEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun watchedMovieDao(): WatchedMovieDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS watchlist_movies")
                db.execSQL("""
                    CREATE TABLE watchlist_movies (
                        id INTEGER NOT NULL,
                        userId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        overview TEXT NOT NULL,
                        releaseDate TEXT,
                        posterPath TEXT,
                        backdropPath TEXT,
                        voteAverage REAL NOT NULL,
                        voteCount INTEGER NOT NULL,
                        genresSerialized TEXT NOT NULL,
                        addedAt INTEGER NOT NULL,
                        PRIMARY KEY (id, userId)
                    )
                """.trimIndent())

                db.execSQL("DROP TABLE IF EXISTS watched_movies")
                db.execSQL("""
                    CREATE TABLE watched_movies (
                        movieId INTEGER NOT NULL,
                        userId TEXT NOT NULL,
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
                        watchedAt INTEGER NOT NULL,
                        PRIMARY KEY (movieId, userId)
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
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}