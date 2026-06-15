package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus

@Entity(tableName = "user_categories")
data class UserCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String = "📁",
    val budget: Double,
    @ColumnInfo(name = "user_id")
    val userId: String?,
    @ColumnInfo(name = "remote_id")
    val remoteId: Long? = null,
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.ACTUALIZADO
)
