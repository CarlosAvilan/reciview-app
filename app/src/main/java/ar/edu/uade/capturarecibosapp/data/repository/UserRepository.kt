package ar.edu.uade.capturarecibosapp.data.repository

import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.local.daos.UserDao
import ar.edu.uade.capturarecibosapp.data.model.User
import ar.edu.uade.capturarecibosapp.data.model.UserPreferences
import ar.edu.uade.capturarecibosapp.data.remote.UserApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.ProfileDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserRepository(
    private val apiService: UserApiService,
    private val userDao: UserDao
) {

    fun getLocalPreferences(): Flow<UserPreferences?> {
        val userId = SessionManager.userId ?: return flowOf(null)
        return userDao.getPreferencesByUserId(userId)
    }

    fun getUserProfile(): Flow<User?> {
        val userId = SessionManager.userId ?: return flowOf(null)
        return userDao.getUserById(userId)
    }

    suspend fun getProfile(): Result<ProfileDTO> {
        val userId = SessionManager.userId ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val response = apiService.getProfile("eq.$userId")
            if (response.isSuccessful) {
                val profile = response.body()?.firstOrNull()
                if (profile != null) {
                    userDao.insertUser(profile.toLocalUser())
                    Result.success(profile)
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

    suspend fun fetchAndCacheProfile(): Result<Unit> = getProfile().fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { Result.failure(it) }
    )

    suspend fun fetchAndCachePreferences(): Result<UserPreferences> {
        val userId = SessionManager.userId ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val response = apiService.getUserPreferences("eq.$userId")
            if (response.isSuccessful) {
                val prefsDto = response.body()?.firstOrNull()
                if (prefsDto != null) {
                    val localPrefs = UserPreferences(
                        notificationsOn = prefsDto.notificationsOn,
                        monthlyMax = prefsDto.monthlyMax,
                        userId = userId
                    )
                    userDao.insertOrUpdatePreferences(localPrefs)
                    Result.success(localPrefs)
                } else {
                    Result.failure(Exception("Preferencias no encontradas en el servidor"))
                }
            } else {
                Result.failure(Exception("Error al obtener preferencias: ${response.code()}"))
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
        val userId = SessionManager.userId ?: return Result.failure(Exception("Usuario no autenticado"))
        val budgetFloat = newBudget.replace(".", "").replace(",", ".").toFloatOrNull()
            ?: return Result.failure(Exception("Presupuesto inválido"))

        userDao.updateBudgetWithStatus(userId, budgetFloat, SyncStatus.PENDIENTE_CAMBIO)

        return try {
            val updateMap = mapOf("monthly_max" to budgetFloat)
            val filters = mapOf("user_id" to "eq.$userId")
            val response = apiService.updateUserPreferences(filters, updateMap)

            if (response.isSuccessful) {
                userDao.updateSyncStatus(userId, SyncStatus.ACTUALIZADO)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al sincronizar con el servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNotifications(enabled: Boolean): Result<Unit> {
        val userId = SessionManager.userId ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val updateMap = mapOf("notifications_on" to enabled)
            val filters = mapOf("user_id" to "eq.$userId")
            val response = apiService.updateUserPreferences(filters, updateMap)
            if (response.isSuccessful) {
                userDao.updateNotifications(userId, enabled)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar notificaciones: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val userId = SessionManager.userId
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.softDeleteProfile("eq.$userId")

            if (response.isSuccessful) {
                userDao.deletePreferencesByUserId(userId)
                userDao.deleteCategoriesByUserId(userId)
                userDao.deleteExpensesByUserId(userId)
                userDao.deleteTicketItemsByUserId(userId)
                userDao.deleteTicketsByUserId(userId)
                userDao.deleteUserById(userId)

                SessionManager.clear()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar la cuenta en el servidor: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ProfileDTO.toLocalUser() = User(
        userId = userId,
        createdAt = "",
        name = name,
        email = email ?: SessionManager.userEmail ?: "",
        phone = phone,
        birth = birth,
        country = country
    )
}
