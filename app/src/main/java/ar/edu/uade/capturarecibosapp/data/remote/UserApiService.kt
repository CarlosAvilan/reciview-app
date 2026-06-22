package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.remote.dto.ProfileDTO
import ar.edu.uade.capturarecibosapp.data.remote.dto.UserPreferencesDTO
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {
    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Query("user_id") userId: String,
        @Query("select") select: String = "*"
    ): Response<List<ProfileDTO>>

    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("user_id") userId: String,
        @Body profile: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @GET("rest/v1/user_preferences")
    suspend fun getUserPreferences(
        @Query("user_id") userId: String,
        @Query("select") select: String = "*"
    ): Response<List<UserPreferencesDTO>>

    @PATCH("rest/v1/user_preferences")
    suspend fun updateUserPreferences(
        @QueryMap filters: Map<String, String>,
        @Body preferences: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>
}
