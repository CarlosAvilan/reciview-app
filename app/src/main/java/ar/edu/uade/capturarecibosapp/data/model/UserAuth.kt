package ar.edu.uade.capturarecibosapp.data.model

data class  UserAuth(
    val uuid: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null
)