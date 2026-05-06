package cst.unibucfmiif2026.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cst.unibucfmiif2026.data.entities.UserEntity
import cst.unibucfmiif2026.data.entities.AddressWithUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<UserEntity>)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("SELECT * FROM users WHERE ${UserEntity.ARG_ADDRESS_ID} = :addressId")
    fun getByAddressId(addressId: Long) : Flow<List<UserEntity>>
}