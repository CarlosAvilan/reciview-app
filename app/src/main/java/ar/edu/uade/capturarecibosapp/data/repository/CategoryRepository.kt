package ar.edu.uade.capturarecibosapp.data.repository

import android.util.Log
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.local.daos.CategoryDao
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.remote.CategoryApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.CategoryDTO
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class CategoryRepository(
    val categoryDao: CategoryDao,
    private val apiService: CategoryApiService,
) {

    private val tag = "CategoryRepository"

    fun getCategories(userId: String): Flow<List<UserCategory>> =
        categoryDao.getCategoriesForUser(userId)

    suspend fun saveCategory(category: UserCategory): Result<Unit> {
        return try {
            Log.d(tag, "Saving category: ${category.name}")
            val toSave = if (category.id == 0L) {
                category.copy(syncStatus = SyncStatus.PENDIENTE_AGREGAR)
            } else {
                // Si ya estaba pendiente de agregar, mantener ese estado
                val current = categoryDao.getCategoryById(category.id)
                if (current?.syncStatus == SyncStatus.PENDIENTE_AGREGAR) {
                    category.copy(syncStatus = SyncStatus.PENDIENTE_AGREGAR)
                } else {
                    category.copy(syncStatus = SyncStatus.PENDIENTE_CAMBIO)
                }
            }
            categoryDao.insertCategory(toSave)
            // Disparar sincronización de fondo
            syncPendingCategories()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error saving category", e)
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(category: UserCategory): Result<Unit> {
        return try {
            Log.d(tag, "Deleting category: ${category.name}")
            if (category.remoteId == null) {
                // Si nunca se sincronizó, borrar directo
                categoryDao.deleteCategoryPhysically(category)
            } else {
                // Borrado lógico para sincronizar luego
                categoryDao.updateSyncStatus(category.id, SyncStatus.PENDIENTE_ELIMINACION)
            }
            syncPendingCategories()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error deleting category", e)
            Result.failure(e)
        }
    }

    suspend fun syncPendingCategories(): Result<Unit> {
        Log.d(tag, "Starting syncPendingCategories")
        return try {
            // Sincronización de bajada (Traer de remoto lo que no tenemos)
            val userId = SessionManager.userId
            if (userId != null) {
                Log.d(tag, "Pulling categories for user: $userId")
                val remoteResponse = apiService.getCategories("eq.$userId")
                if (remoteResponse.isSuccessful) {
                    val remoteItems = remoteResponse.body() ?: emptyList()
                    Log.d(tag, "Pulled ${remoteItems.size} categories from remote")
                    remoteItems.forEach { dto ->
                        val remoteId = dto.id ?: return@forEach
                        
                        // 1. Intentar encontrar por remote_id
                        val existingByRemoteId = categoryDao.getCategoryByRemoteId(remoteId)
                        
                        if (existingByRemoteId == null) {
                            // 2. Si no existe por remote_id, buscar por nombre para evitar duplicados si se creó offline
                            val existingByName = categoryDao.getCategoryByName(dto.name, userId)
                            
                            if (existingByName != null && existingByName.remoteId == null) {
                                Log.d(tag, "Linking local category '${dto.name}' with remoteId $remoteId")
                                // Vincular la categoría local con la remota
                                categoryDao.updateCategory(
                                    existingByName.copy(
                                        remoteId = remoteId,
                                        syncStatus = SyncStatus.ACTUALIZADO,
                                        budget = dto.budget,
                                        icon = dto.icon ?: existingByName.icon
                                    )
                                )
                            } else if (existingByName == null) {
                                Log.d(tag, "Inserting new remote category '${dto.name}'")
                                // Insertar como nueva si no existe ni por ID ni por nombre
                                val category = UserCategory(
                                    name = dto.name,
                                    budget = dto.budget,
                                    icon = dto.icon ?: "📁",
                                    userId = dto.userId,
                                    remoteId = remoteId,
                                    syncStatus = SyncStatus.ACTUALIZADO
                                )
                                categoryDao.insertCategory(category)
                            }
                        } else if (existingByRemoteId.syncStatus == SyncStatus.ACTUALIZADO) {
                            // Solo actualizar si no hay cambios locales pendientes
                            categoryDao.updateCategory(existingByRemoteId.copy(
                                name = dto.name,
                                budget = dto.budget,
                                icon = dto.icon ?: existingByRemoteId.icon
                            ))
                        }
                    }
                } else {
                    Log.e(tag, "Failed to pull categories: ${remoteResponse.code()}")
                }
            }

            val pending = categoryDao.getPendingSyncCategories()
            Log.d(tag, "Found ${pending.size} pending categories to push")
            pending.forEach { local ->
                when (local.syncStatus) {
                    SyncStatus.PENDIENTE_AGREGAR -> {
                        Log.d(tag, "Pushing new category: ${local.name}")
                        val dto = local.toDTO()
                        val response = apiService.createCategory(dto)
                        if (response.isSuccessful) {
                            val remote = response.body()?.firstOrNull()
                            if (remote?.id != null) {
                                Log.d(tag, "Category '${local.name}' synced successfully with remoteId ${remote.id}")
                                categoryDao.updateRemoteIdAfterSync(local.id, remote.id)
                            }
                        } else {
                            Log.e(tag, "Error creating category '${local.name}': ${response.code()}")
                        }
                    }
                    SyncStatus.PENDIENTE_CAMBIO -> {
                        Log.d(tag, "Pushing update for category: ${local.name}")
                        local.remoteId?.let { remoteId ->
                            val updateMap = mapOf(
                                "name" to local.name,
                                "budget" to local.budget,
                                "icon" to local.icon
                            )
                            val response = apiService.updateCategory("eq.$remoteId", updateMap)
                            if (response.isSuccessful) {
                                Log.d(tag, "Category '${local.name}' updated successfully")
                                categoryDao.updateSyncStatus(local.id, SyncStatus.ACTUALIZADO)
                            } else {
                                Log.e(tag, "Error updating category '${local.name}': ${response.code()}")
                            }
                        }
                    }
                    SyncStatus.PENDIENTE_ELIMINACION -> {
                        Log.d(tag, "Pushing deletion for category: ${local.name}")
                        local.remoteId?.let { remoteId ->
                            val response = apiService.deleteCategory("eq.$remoteId")
                            if (response.isSuccessful || response.code() == 404) {
                                Log.d(tag, "Category '${local.name}' deleted remotely")
                                categoryDao.deleteCategoryPhysically(local)
                            } else {
                                Log.e(tag, "Error deleting category '${local.name}': ${response.code()}")
                            }
                        } ?: run {
                            categoryDao.deleteCategoryPhysically(local)
                        }
                    }
                    else -> {}
                }
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Log.e(tag, "Network error during sync", e)
            // Error de red, falla silenciosamente para mantener modo offline
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(tag, "Unexpected error during sync", e)
            Result.failure(e)
        }
    }

    private fun UserCategory.toDTO() = CategoryDTO(
        id = remoteId,
        name = name,
        budget = budget,
        icon = icon,
        userId = userId ?: ""
    )
}
