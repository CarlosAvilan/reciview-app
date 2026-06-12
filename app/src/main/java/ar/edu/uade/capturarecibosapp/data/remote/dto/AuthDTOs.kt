package ar.edu.uade.capturarecibosapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO para la petición de Auth
data class AuthRequestDTO(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("data") val data: Map<String, Any>? = null
)

// DTO para la respuesta de Supabase Auth
// Basado en la respuesta real: {"access_token":"...", "user":{"id":"...", "email":"..."}}
data class AuthResponseDTO(
    @SerializedName("user") val user: UserInfoDTO? = null,
    @SerializedName("id") val directId: String? = null, // fallback
    @SerializedName("email") val directEmail: String? = null, // fallback
    @SerializedName("access_token") val accessToken: String? = null
) {
    // Helpers para obtener los datos sin importar la estructura
    val id: String? get() = user?.id ?: directId
    val email: String? get() = user?.email ?: directEmail
}

data class UserInfoDTO(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String
)

// DTO para la tabla pública 'profiles'
data class ProfileDTO(
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("birth") val birth: String,
    @SerializedName("country") val country: String? = null,
    @SerializedName("phone") val phone: String? = null
)
