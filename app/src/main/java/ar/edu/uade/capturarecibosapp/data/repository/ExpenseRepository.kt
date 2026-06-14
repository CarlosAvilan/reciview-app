package ar.edu.uade.capturarecibosapp.data.repository

import ar.edu.uade.capturarecibosapp.data.model.CategoryDto
import ar.edu.uade.capturarecibosapp.data.model.ExpenseDto
import ar.edu.uade.capturarecibosapp.data.remote.ExpenseApiService

class ExpenseRepository(private val apiService: ExpenseApiService) {

    suspend fun getExpenses(): Result<List<ExpenseDto>> {
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

    suspend fun insertExpense(expense: ExpenseDto): Result<Unit> {
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
