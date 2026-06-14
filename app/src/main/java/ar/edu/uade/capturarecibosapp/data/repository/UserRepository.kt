package ar.edu.uade.capturarecibosapp.data.repository

import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.remote.UserApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.ProfileDTO

class UserRepository(private val apiService: UserApiService) {

    suspend fun getProfile(): Result<ProfileDTO> {
        val userId = SessionManager.userId ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val response = apiService.getProfile("eq.$userId")
            if (response.isSuccessful) {
                val profiles = response.body()
                if (!profiles.isNullOrEmpty()) {
                    Result.success(profiles[0])
                } else {
                    Result.failure(Exception("Perfil no encontrado"))
                }
            } else {
                Result.failure(Exception("Error al obtener perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(name: String, birth: String, country: String, phone: String): Result<Unit> {
        val userId = SessionManager.userId ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val updateMap = mapOf(
                "name" to name,
                "birth" to birth,
                "country" to country,
                "phone" to phone
            )
            val response = apiService.updateProfile("eq.$userId", updateMap)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBudget(newBudget: String): Result<Unit> {
        return Result.success(Unit)
    }
}
