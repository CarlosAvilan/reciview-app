package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense_item ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseItem>>

    @Query("SELECT * FROM expense_item WHERE user_id = :userId ORDER BY date DESC")
    fun getExpensesForUser(userId: String): Flow<List<ExpenseItem>>

    @Query("SELECT SUM(amount) FROM expense_item WHERE user_id = :userId AND category = :categoryName")
    fun getTotalSpentByCategory(userId: String, categoryName: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseItem>)

    @Delete
    suspend fun deleteExpense(expense: ExpenseItem)
}
