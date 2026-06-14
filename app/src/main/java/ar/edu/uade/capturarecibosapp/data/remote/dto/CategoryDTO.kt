package ar.edu.uade.capturarecibosapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryDTO(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("budget")
    val budget: Double,
    @SerializedName("user_id")
    val userId: String
)
