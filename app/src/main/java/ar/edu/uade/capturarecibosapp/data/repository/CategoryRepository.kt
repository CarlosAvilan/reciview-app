package ar.edu.uade.capturarecibosapp.data.repository

import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.local.daos.CategoryDao
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.remote.CategoryApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.CategoryDTO
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val apiService: CategoryApiService
) {

    fun getCategories(userId: String): Flow<List<UserCategory>> =
        categoryDao.getCategoriesForUser(userId)

    suspend fun saveCategory(category: UserCategory): Result<Unit> {
        return try {
            if (category.id == 0L) {
                categoryDao.insertCategory(category.copy(syncStatus = SyncStatus.PENDIENTE_AGREGAR))
            } else {
                categoryDao.updateCategory(category.copy(syncStatus = SyncStatus.PENDIENTE_CAMBIO))
            }
            // Disparar sincronización de fondo
            syncPendingCategories()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(category: UserCategory): Result<Unit> {
        return try {
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
            Result.failure(e)
        }
    }

    suspend fun syncPendingCategories(): Result<Unit> {
        return try {
            val pending = categoryDao.getPendingSyncCategories()
            pending.forEach { local ->
                when (local.syncStatus) {
                    SyncStatus.PENDIENTE_AGREGAR -> {
                        val dto = local.toDTO()
                        val response = apiService.createCategory(dto)
                        if (response.isSuccessful) {
                            val remote = response.body()?.firstOrNull()
                            if (remote?.id != null) {
                                categoryDao.updateRemoteIdAfterSync(local.id, remote.id)
                            }
                        }
                    }
                    SyncStatus.PENDIENTE_CAMBIO -> {
                        local.remoteId?.let { remoteId ->
                            val updateMap = mapOf(
                                "name" to local.name,
                                "budget" to local.budget
                            )
                            val response = apiService.updateCategory("eq.$remoteId", updateMap)
                            if (response.isSuccessful) {
                                categoryDao.updateSyncStatus(local.id, SyncStatus.ACTUALIZADO)
                            }
                        }
                    }
                    SyncStatus.PENDIENTE_ELIMINACION -> {
                        local.remoteId?.let { remoteId ->
                            val response = apiService.deleteCategory("eq.$remoteId")
                            if (response.isSuccessful || response.code() == 404) {
                                categoryDao.deleteCategoryPhysically(local)
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
            // Error de red, falla silenciosamente para mantener modo offline
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun UserCategory.toDTO() = CategoryDTO(
        id = remoteId,
        name = name,
        budget = budget,
        userId = userId ?: ""
    )
}
