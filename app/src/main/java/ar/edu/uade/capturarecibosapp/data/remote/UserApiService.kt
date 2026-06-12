package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.remote.dto.ProfileDTO
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {
    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Query("user_id") userId: String, // Cambiado de 'id' a 'user_id' según esquema Supabase
        @Query("select") select: String = "*"
    ): Response<List<ProfileDTO>>

    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("user_id") userId: String, // Cambiado de 'id' a 'user_id'
        @Body profile: Map<String, Any>
    ): Response<Unit>
}
