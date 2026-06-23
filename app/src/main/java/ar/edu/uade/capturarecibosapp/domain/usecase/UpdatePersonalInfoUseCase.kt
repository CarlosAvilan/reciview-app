package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.repository.UserRepository
import ar.edu.uade.capturarecibosapp.domain.InputValidator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UpdatePersonalInfoUseCase(
    private val userRepository: UserRepository
) {
    sealed class Result {
        object Success : Result()
        data class ValidationError(
            val nameError: String?,
            val phoneError: String?,
            val birthDateError: String?
        ) : Result()
        data class Failure(val message: String) : Result()
    }

    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend operator fun invoke(
        nombre: String,
        fechaNacimiento: LocalDate?,
        paisResidencia: String,
        telefono: String
    ): Result {
        val nameError = if (nombre.isBlank()) "El nombre no puede estar vacío" else null
        val phoneError = if (!InputValidator.isValidPhone(telefono)) "Teléfono inválido (mínimo 7 dígitos)" else null
        val birthDateError = if (fechaNacimiento != null && !fechaNacimiento.isBefore(LocalDate.now()))
            "La fecha de nacimiento no puede ser una fecha futura" else null

        if (nameError != null || phoneError != null || birthDateError != null) {
            return Result.ValidationError(nameError, phoneError, birthDateError)
        }

        val birthString = fechaNacimiento?.format(apiDateFormatter) ?: ""

        val result = userRepository.updateProfile(
            name = nombre,
            birth = birthString,
            country = paisResidencia,
            phone = telefono
        )
        return if (result.isSuccess) Result.Success
        else Result.Failure("Error al actualizar perfil")
    }
}
