package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.*
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM user_categories WHERE user_id = :userId AND sync_status != 'PENDIENTE_ELIMINACION'")
    fun getCategoriesForUser(userId: String): Flow<List<UserCategory>>

    @Query("SELECT * FROM user_categories WHERE sync_status != 'ACTUALIZADO'")
    suspend fun getPendingSyncCategories(): List<UserCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: UserCategory): Long

    @Update
    suspend fun updateCategory(category: UserCategory)

    @Delete
    suspend fun deleteCategoryPhysically(category: UserCategory)

    @Query("UPDATE user_categories SET sync_status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: SyncStatus)

    @Query("UPDATE user_categories SET remote_id = :remoteId, sync_status = 'ACTUALIZADO' WHERE id = :localId")
    suspend fun updateRemoteIdAfterSync(localId: Long, remoteId: Long)

    @Query("SELECT * FROM user_categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): UserCategory?

    @Query("SELECT * FROM user_categories WHERE id = :id")
    fun getCategoryByIdFlow(id: Long): Flow<UserCategory?>

    @Query("SELECT * FROM user_categories WHERE remote_id = :remoteId")
    suspend fun getCategoryByRemoteId(remoteId: Long): UserCategory?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExist(category: UserCategory): Long

    @Query("DELETE FROM user_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM user_categories WHERE name = :name AND (user_id = :userId OR user_id IS NULL) LIMIT 1")
    suspend fun getCategoryByName(name: String, userId: String): UserCategory?
}
