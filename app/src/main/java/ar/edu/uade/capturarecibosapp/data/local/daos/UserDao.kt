package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.uade.capturarecibosapp.data.model.User
import ar.edu.uade.capturarecibosapp.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Perfil de usuario
    @Query("SELECT * FROM profiles WHERE user_id = :userId")
    suspend fun getUserById(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Preferencias
    @Query("SELECT * FROM user_preferences WHERE user_id = :userId")
    fun getPreferencesByUserId(userId: String): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: UserPreferences)
}