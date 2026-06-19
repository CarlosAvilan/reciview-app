package ar.edu.uade.capturarecibosapp.data.repository

import ar.edu.uade.capturarecibosapp.data.local.daos.CategoryDao
import ar.edu.uade.capturarecibosapp.data.local.daos.ExpenseDao
import ar.edu.uade.capturarecibosapp.data.local.daos.TicketDao
import ar.edu.uade.capturarecibosapp.data.model.CategoryDto
import ar.edu.uade.capturarecibosapp.data.model.ExpenseDto
import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.remote.ExpenseApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ExpenseRepository(
    val expenseDao: ExpenseDao,
    val categoryDao: CategoryDao,
    val ticketDao: TicketDao,
    private val apiService: ExpenseApiService,
) {

    fun getExpensesForUser(userId: String): Flow<List<ExpenseItem>> {
        return expenseDao.getExpensesForUser(userId)
    }

    fun getCategoryByName(userId: String, name: String): Flow<UserCategory?> {
        // Implementación rápida o buscar en el DAO de categorías
        // Para simplificar el ViewModel, asumiremos que el repositorio puede proveer esto
        return categoryDao.getCategoriesForUser(userId).map { list ->
            list.find { it.name == name }
        }
    }

    fun getTotalSpentByCategory(userId: String, categoryName: String): Flow<Double> {
        val manualSpentFlow = expenseDao.getTotalSpentByCategory(userId, categoryName)
        val ticketSpentFlow = ticketDao.getTotalSpentFromTickets(userId, categoryName)
        
        return combine(manualSpentFlow, ticketSpentFlow) { manual, ticket ->
            (manual ?: 0.0) + (ticket ?: 0.0)
        }
    }

    suspend fun saveExpense(expense: ExpenseItem): Result<Unit> {
        return try {
            expenseDao.insertExpense(expense)
            // Sincronizar con remoto si fuera necesario
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpensesRemote(): Result<List<ExpenseDto>> {
        return try {
            val response = apiService.getExpenses()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener gastos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<CategoryDto>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener categorías"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertExpenseRemote(expense: ExpenseDto): Result<Unit> {
        return try {
            val response = apiService.insertExpense(expense)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al insertar gasto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteExpense(id.toString())
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar gasto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
