package ar.edu.uade.capturarecibosapp.data.repository

import androidx.compose.runtime.LaunchedEffect
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.local.daos.UserDao
import ar.edu.uade.capturarecibosapp.data.model.UserPreferences
import ar.edu.uade.capturarecibosapp.data.remote.UserApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.ProfileDTO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val apiService: UserApiService,
    private val userDao: UserDao
) {

    fun getLocalPreferences(): Flow<UserPreferences?> {
        val userId = SessionManager.userId ?: return kotlinx.coroutines.flow.flowOf(null)
        return userDao.getPreferencesByUserId(userId)
    }

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

        // 1. Actualización local inmediata (Offline-First)
        userDao.updateBudgetWithStatus(userId, budgetFloat, SyncStatus.PENDIENTE_CAMBIO)

        return try {
            // 2. Intento de sincronización remota
            val updateMap = mapOf("monthly_max" to budgetFloat)
            // Usamos QueryMap para que el parámetro se envíe exactamente como 'user_id=eq.UUID'
            val filters = mapOf("user_id" to "eq.$userId")
            val response = apiService.updateUserPreferences(filters, updateMap)
            
            if (response.isSuccessful) {
                // 3. Sincronización exitosa
                userDao.updateSyncStatus(userId, SyncStatus.ACTUALIZADO)
                Result.success(Unit)
            } else {
                // 4. Error de servidor (se queda como pendiente para reintento manual o por Worker)
                Result.failure(Exception("Error al sincronizar con el servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            // 5. Error de red (se queda como pendiente)
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

            // Llamada al Backend
            // val response = apiService.deleteAccount(userId)
            // if (!response.isSuccessful) return Result.failure(Exception("Error en servidor"))

            // Eliminar datos locales en Room
            userDao.deletePreferencesByUserId(userId)
            userDao.deleteCategoriesByUserId(userId)
            userDao.deleteExpensesByUserId(userId)
            userDao.deleteTicketItemsByUserId(userId)
            userDao.deleteTicketsByUserId(userId)
            userDao.deleteUserById(userId)

            // Limpiar las credenciales y estado de sesión global
            SessionManager.clear()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
