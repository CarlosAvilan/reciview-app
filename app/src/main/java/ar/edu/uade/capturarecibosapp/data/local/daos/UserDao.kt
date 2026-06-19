package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.model.User
import ar.edu.uade.capturarecibosapp.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM profiles WHERE user_id = :userId")
    fun getUserById(userId: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Preferencias
    @Query("SELECT * FROM user_preferences WHERE user_id = :userId")
    fun getPreferencesByUserId(userId: String): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: UserPreferences)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(prefs: UserPreferences)

    // Actualizaciones desde la App
    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE user_preferences SET notifications_on = :enabled WHERE user_id = :userId")
    suspend fun updateNotifications(userId: String, enabled: Boolean)

    @Query("UPDATE user_preferences SET monthly_max = :max, sync_status = :status WHERE user_id = :userId")
    suspend fun updateBudgetWithStatus(userId: String, max: Float, status: SyncStatus)

    @Query("UPDATE user_preferences SET sync_status = :status WHERE user_id = :userId")
    suspend fun updateSyncStatus(userId: String, status: SyncStatus)
}