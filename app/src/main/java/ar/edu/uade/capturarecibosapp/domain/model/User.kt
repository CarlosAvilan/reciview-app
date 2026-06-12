package ar.edu.uade.capturarecibosapp.domain.model

data class User(
    val uuid: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null
)
