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

    @Query("DELETE FROM watched_movies WHERE movieId = :movieId AND userId = :userId")
    suspend fun deleteById(movieId: Int, userId: String)

    @Query("SELECT * FROM watched_movies WHERE movieId = :movieId AND userId = :userId LIMIT 1")
    suspend fun getById(movieId: Int, userId: String): WatchedMovieEntity?

    @Query("SELECT * FROM watched_movies WHERE userId = :userId ORDER BY watchedAt DESC")
    fun observeAll(userId: String): Flow<List<WatchedMovieEntity>>

    @Query("SELECT * FROM watched_movies WHERE userId = :userId AND isFavorite = 1 ORDER BY watchedAt DESC")
    fun observeFavorites(userId: String): Flow<List<WatchedMovieEntity>>

    @Query("SELECT * FROM watched_movies WHERE userId = :userId ORDER BY rating DESC")
    fun observeAllSortedByRating(userId: String): Flow<List<WatchedMovieEntity>>

    @Query("SELECT movieId FROM watched_movies WHERE userId = :userId")
    fun observeWatchedIds(userId: String): Flow<List<Int>>

    @Query("UPDATE watched_movies SET isFavorite = :isFavorite WHERE movieId = :movieId AND userId = :userId")
    suspend fun updateFavorite(movieId: Int, userId: String, isFavorite: Boolean)

    @Query("UPDATE watched_movies SET rating = :rating, comment = :comment WHERE movieId = :movieId AND userId = :userId")
    suspend fun updateReview(movieId: Int, userId: String, rating: Int, comment: String)
}