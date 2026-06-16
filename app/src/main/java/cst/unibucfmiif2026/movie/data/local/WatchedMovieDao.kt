package cst.unibucfmiif2026.movie.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: WatchedMovieEntity)

    @Query("DELETE FROM watched_movies WHERE movieId = :movieId")
    suspend fun deleteById(movieId: Int)

    @Query("SELECT * FROM watched_movies WHERE movieId = :movieId LIMIT 1")
    suspend fun getById(movieId: Int): WatchedMovieEntity?

    @Query("SELECT * FROM watched_movies ORDER BY watchedAt DESC")
    fun observeAll(): Flow<List<WatchedMovieEntity>>

    @Query("SELECT * FROM watched_movies WHERE isFavorite = 1 ORDER BY watchedAt DESC")
    fun observeFavorites(): Flow<List<WatchedMovieEntity>>

    @Query("SELECT * FROM watched_movies ORDER BY rating DESC")
    fun observeAllSortedByRating(): Flow<List<WatchedMovieEntity>>

    @Query("SELECT movieId FROM watched_movies")
    fun observeWatchedIds(): Flow<List<Int>>

    @Query("UPDATE watched_movies SET isFavorite = :isFavorite WHERE movieId = :movieId")
    suspend fun updateFavorite(movieId: Int, isFavorite: Boolean)

    @Query("UPDATE watched_movies SET rating = :rating, comment = :comment WHERE movieId = :movieId")
    suspend fun updateReview(movieId: Int, rating: Int, comment: String)
}