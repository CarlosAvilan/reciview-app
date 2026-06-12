package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY month DESC")
    fun getAllMonthlyReports(): Flow<List<MonthlyReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyReport(report: MonthlyReport)

    @Query("DELETE FROM reports")
    suspend fun clearAllReports()
}