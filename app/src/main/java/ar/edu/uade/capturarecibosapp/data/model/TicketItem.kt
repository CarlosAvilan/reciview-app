package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class TicketItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "ticket_id")
    val ticketId: Long,
    val commerce: String,
    val date: String,
    val amount: Float,
    val category: String = "Otros",
    val photoUrl: String? = null,
    val description: String = ""
)
