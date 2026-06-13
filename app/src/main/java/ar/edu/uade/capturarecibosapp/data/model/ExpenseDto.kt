package ar.edu.uade.capturarecibosapp.data.model

import com.google.gson.annotations.SerializedName

data class ExpenseDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: String,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("user_id") val userId: String? = null
)

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("icon_url") val iconUrl: String? = null
)
