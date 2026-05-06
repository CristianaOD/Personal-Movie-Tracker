package cst.unibucfmiif2026.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ARG_ID)
    val id : Long = 0,
    @ColumnInfo(name="first_name")
    val firstName : String,
    @ColumnInfo(name="last_name")
    val lastName : String,
    @ColumnInfo(name = ARG_ADDRESS_ID)
    val addressId : Long? = null,
    val email : String? = null,
    val avatar : String? = null
) {
    companion object {
        const val ARG_ID = "id"
        const val ARG_ADDRESS_ID = "address_id"
    }
}
