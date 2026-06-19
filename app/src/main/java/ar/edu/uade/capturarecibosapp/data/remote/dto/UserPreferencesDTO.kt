package ar.edu.uade.capturarecibosapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserPreferencesDTO(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("user_id") val userId: String,
    @SerializedName("notifications_on") val notificationsOn: Boolean,
    @SerializedName("monthly_max") val monthlyMax: Float
)
