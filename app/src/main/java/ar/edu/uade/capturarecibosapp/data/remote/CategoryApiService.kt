package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.remote.dto.CategoryDTO
import retrofit2.Response
import retrofit2.http.*

interface CategoryApiService {
    @GET("rest/v1/user_categories")
    suspend fun getCategories(
        @Query("user_id") userIdFilter: String,
        @Query("select") select: String = "*",
    ): Response<List<CategoryDTO>>

    @POST("rest/v1/user_categories")
    @Headers("Prefer: return=representation")
    suspend fun createCategory(
        @Body category: CategoryDTO
    ): Response<List<CategoryDTO>>

    @PATCH("rest/v1/user_categories")
    @Headers("Prefer: return=minimal")
    suspend fun updateCategory(
        @Query("id") idFilter: String,
        @Body category: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @DELETE("rest/v1/user_categories")
    suspend fun deleteCategory(
        @Query("id") idFilter: String
    ): Response<Unit>

    @DELETE("rest/v1/user_categories")
    suspend fun deleteCategoriesByUserId(
        @Query("user_id") userIdFilter: String
    ): Response<Unit>
}
