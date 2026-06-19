package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "notifications_on")
    val notificationsOn: Boolean,
    @ColumnInfo(name = "monthly_max")
    val monthlyMax: Float,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.ACTUALIZADO
)
