package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.remote.dto.AuthRequestDTO
import ar.edu.uade.capturarecibosapp.data.remote.dto.AuthResponseDTO
import ar.edu.uade.capturarecibosapp.data.remote.dto.ProfileDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {
    @POST("auth/v1/signup")
    suspend fun signUp(@Body request: AuthRequestDTO): Response<AuthResponseDTO>

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(@Body request: AuthRequestDTO): Response<AuthResponseDTO>

    @PUT("auth/v1/user")
    suspend fun changePassword(@Body request: Map<String,String>): Response<AuthResponseDTO>

    @POST("auth/v1/recover")
    suspend fun recoverPassword(@Body body: Map<String, String>): Response<Unit>
}
