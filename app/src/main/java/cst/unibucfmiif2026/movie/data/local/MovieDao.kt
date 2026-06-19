package cst.unibucfmiif2026.movie.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    @Query("DELETE FROM watchlist_movies WHERE id = :movieId AND userId = :userId")
    suspend fun deleteById(movieId: Int, userId: String)

    @Query("SELECT * FROM watchlist_movies WHERE id = :movieId AND userId = :userId LIMIT 1")
    suspend fun getById(movieId: Int, userId: String): MovieEntity?

    @Query("SELECT * FROM watchlist_movies WHERE userId = :userId ORDER BY addedAt DESC")
    fun observeWatchlistMovies(userId: String): Flow<List<MovieEntity>>

    @Query("SELECT id FROM watchlist_movies WHERE userId = :userId")
    fun observeWatchlistIds(userId: String): Flow<List<Int>>
}