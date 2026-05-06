package cst.unibucfmiif2026.network.dto

import com.google.gson.annotations.SerializedName
import cst.unibucfmiif2026.data.entities.UserEntity

data class UserDTO(
    val id: Long,
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val avatar: String
)

fun UserDTO.toEntity(addressId : Long) = UserEntity(
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    avatar = this.avatar,
    addressId = addressId
)

data class UsersResponse(
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    val data: List<UserDTO>
)

//{
//    "page": 2,
//    "per_page": 6,
//    "total": 12,
//    "total_pages": 2,
//    "data": [
//        {
//            "id": 7,
//            "email": "michael.lawson@reqres.in",
//            "first_name": "Michael",
//            "last_name": "Lawson",
//            "avatar": "https://reqres.in/img/faces/7-image.jpg"
//        },
