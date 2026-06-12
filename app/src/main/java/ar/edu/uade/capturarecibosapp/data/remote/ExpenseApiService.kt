package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.model.CategoryDto
import ar.edu.uade.capturarecibosapp.data.model.ExpenseDto
import retrofit2.Response
import retrofit2.http.*

interface ExpenseApiService {
    @GET("expenses")
    suspend fun getExpenses(): Response<List<ExpenseDto>>

    @POST("expenses")
    suspend fun insertExpense(@Body expense: ExpenseDto): Response<Unit>

    @DELETE("expenses")
    suspend fun deleteExpense(@Query("id") id: String): Response<Unit>

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>
}
