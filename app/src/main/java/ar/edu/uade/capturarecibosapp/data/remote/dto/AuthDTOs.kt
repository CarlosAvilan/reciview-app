package ar.edu.uade.capturarecibosapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO para la petición de Auth
data class AuthRequestDTO(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("data") val data: Map<String, Any>? = null
)

// DTO para la respuesta de Supabase Auth
data class AuthResponseDTO(
    @SerializedName("user") val user: UserInfoDTO? = null,
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("expires_in") val expiresIn: Int? = null
) {
    val id: String? get() = user?.id
    val email: String? get() = user?.email
}

data class UserInfoDTO(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String
)

// DTO para la tabla pública 'profiles'
data class ProfileDTO(
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String? = null, // Cambiado a nullable
    @SerializedName("birth") val birth: String,
    @SerializedName("country") val country: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("deleted") val deleted: Boolean? = false
)
