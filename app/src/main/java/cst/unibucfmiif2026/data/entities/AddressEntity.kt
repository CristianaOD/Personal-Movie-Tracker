package cst.unibucfmiif2026.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ARG_ID)
    val id: Long = 0,
    @ColumnInfo(name = "street_name")
    val street: String,
    @ColumnInfo(name = "city")
    val city: String,
    @ColumnInfo(name = "country")
    val country: String
) {
    companion object {
        const val ARG_ID = "id"
    }
}
