package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("UPDATE user_preferences SET monthly_max = :max WHERE user_id = :userId")
    suspend fun updateBudget(userId: String, max: Float)
}