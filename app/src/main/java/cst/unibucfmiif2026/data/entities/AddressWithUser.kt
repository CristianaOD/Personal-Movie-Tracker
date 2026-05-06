package cst.unibucfmiif2026.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class AddressWithUser(
    @Embedded
    val address : AddressEntity,
    @Relation(
        parentColumn = AddressEntity.ARG_ID,
        entityColumn = UserEntity.ARG_ADDRESS_ID
    )
    val user : UserEntity
)
