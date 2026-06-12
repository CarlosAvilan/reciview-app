package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class MonthlyReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val month: String,
    val amount: Float,
    @ColumnInfo(name = "average_cost")
    val averageCost: String,
    @ColumnInfo(name = "most_active_day")
    val mostActiveDay: String
)