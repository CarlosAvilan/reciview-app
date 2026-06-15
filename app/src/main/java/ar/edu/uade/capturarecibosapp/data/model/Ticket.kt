package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long?,
    val establishment: String,
    val amount: Float,
    @ColumnInfo(name = "photo_url")
    val photoUrl: String?,
    val description: String = "",
    @ColumnInfo(name = "remote_id")
    val remoteId: Long? = null,
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.ACTUALIZADO
)
