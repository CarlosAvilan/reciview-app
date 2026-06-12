package ar.edu.uade.capturarecibosapp.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
