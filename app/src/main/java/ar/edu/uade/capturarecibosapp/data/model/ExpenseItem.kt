package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_item")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "photo_url")
    val photoUrl: String? = null,
    @ColumnInfo(name = "user_id")
    val userId: String,
    val title: String,
    val date: String,
    val category: String,
    val amount: Double
)