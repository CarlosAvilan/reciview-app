package ar.edu.uade.capturarecibosapp.data.remote.dto

import ar.edu.uade.capturarecibosapp.data.model.UserAuth

fun AuthResponseDTO.toDomain() = UserAuth(
    uuid = this.id ?: "",
    email = this.email ?: ""
)
