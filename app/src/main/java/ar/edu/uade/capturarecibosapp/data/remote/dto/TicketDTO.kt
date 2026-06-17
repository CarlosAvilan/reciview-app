package ar.edu.uade.capturarecibosapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TicketDTO(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("category_id")
    val categoryId: Long?,
    @SerializedName("establishment")
    val establishment: String,
    @SerializedName("amount")
    val amount: Float,
    @SerializedName("photo_url")
    val photoUrl: String?,
    @SerializedName("description")
    val description: String
)
