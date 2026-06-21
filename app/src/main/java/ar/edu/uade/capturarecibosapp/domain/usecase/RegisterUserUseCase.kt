package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Encapsula la regla de "qué es un registro válido":
 * password mínimo, email con formato básico, fecha de nacimiento
 * no futura, y términos aceptados.
 */
class RegisterUserUseCase(
    private val authRepository: AuthRepository
) {
    sealed class Result {
        data class Success(val email: String) : Result()
        data class PasswordError(val message: String) : Result()
        data class EmailError(val message: String) : Result()
        data class BirthDateError(val message: String) : Result()
        data class TermsError(val message: String) : Result()
        data class Failure(val message: String) : Result()
    }

    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        birth: LocalDate,
        country: String,
        termsAccepted: Boolean
    ): Result {
        if (password.length < 6) {
            return Result.PasswordError("Contraseña débil (mínimo 6 caracteres)")
        }

        if (email.isEmpty() || !email.contains("@")) {
            return Result.EmailError("Ingresa un correo válido")
        }

        if (!birth.isBefore(LocalDate.now())) {
            return Result.BirthDateError("La fecha de nacimiento no puede ser hoy ni una fecha futura");
        }

        val result = authRepository.registerUser(
            email = email,
            pass = password,
            name = name,
            birth = birth.format(apiDateFormatter),
            country = country
        )

        return result.fold(
            onSuccess = { Result.Success(it.email) },
            onFailure = { error ->
                val message = error.message ?: ""
                if (message.contains("already exists", ignoreCase = true)) {
                    Result.EmailError("El usuario ya existe")
                } else {
                    Result.Failure(message)
                }
            }
        )
    }
}