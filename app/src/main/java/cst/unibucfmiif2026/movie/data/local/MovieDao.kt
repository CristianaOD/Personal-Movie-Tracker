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

    @Query("DELETE FROM watchlist_movies WHERE id = :movieId")
    suspend fun deleteById(movieId: Int)

    @Query("SELECT * FROM watchlist_movies WHERE id = :movieId LIMIT 1")
    suspend fun getById(movieId: Int): MovieEntity?

    @Query("SELECT * FROM watchlist_movies ORDER BY addedAt DESC")
    fun observeWatchlistMovies(): Flow<List<MovieEntity>>

    @Query("SELECT id FROM watchlist_movies")
    fun observeWatchlistIds(): Flow<List<Int>>
}
