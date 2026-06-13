package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advice_items")
data class AdviceItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    @ColumnInfo(name = "icon")
    val icon: String
)