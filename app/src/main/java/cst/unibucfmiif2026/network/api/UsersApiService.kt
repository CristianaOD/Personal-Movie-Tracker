package cst.unibucfmiif2026.network.api

import cst.unibucfmiif2026.network.dto.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApiService {
    @GET("api/users")
    suspend fun getUsers(
        @Query("page") page: Int
    ) : UsersResponse
}