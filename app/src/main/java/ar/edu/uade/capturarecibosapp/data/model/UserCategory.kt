package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_categories")
data class UserCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val budget: Float,
    @ColumnInfo(name = "user_id")
    val userId: String?
)
