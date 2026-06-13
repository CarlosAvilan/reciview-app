package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.remote.dto.AuthRequestDTO
import ar.edu.uade.capturarecibosapp.data.remote.dto.AuthResponseDTO
import ar.edu.uade.capturarecibosapp.data.remote.dto.ProfileDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    // PASO 1: Auth (Sin el prefijo rest/v1/)
    @POST("auth/v1/signup")
    suspend fun signUp(@Body request: AuthRequestDTO): Response<AuthResponseDTO>

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(@Body request: AuthRequestDTO): Response<AuthResponseDTO>

    @POST("auth/v1/recover")
    suspend fun recoverPassword(@Body body: Map<String, String>): Response<Unit>

    // PASO 2: Perfil Público (Con el prefijo rest/v1/)
    @POST("rest/v1/profiles")
    suspend fun createProfile(@Body profile: ProfileDTO): Response<Unit>
}
