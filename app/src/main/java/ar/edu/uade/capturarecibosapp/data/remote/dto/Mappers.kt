package ar.edu.uade.capturarecibosapp.data.remote.dto

import ar.edu.uade.capturarecibosapp.domain.model.User

fun AuthResponseDTO.toDomain() = User(
    uuid = this.id ?: "",
    email = this.email ?: ""
)
