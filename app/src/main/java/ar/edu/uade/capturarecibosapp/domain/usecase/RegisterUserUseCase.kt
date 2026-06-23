package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository
import ar.edu.uade.capturarecibosapp.domain.InputValidator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RegisterUserUseCase(
    private val authRepository: AuthRepository
) {
    sealed class Result {
        data class Success(val email: String) : Result()
        data class NameError(val message: String) : Result()
        data class EmailError(val message: String) : Result()
        data class PasswordError(val message: String) : Result()
        data class CountryError(val message: String) : Result()
        data class BirthDateError(val message: String) : Result()
        data class TermsError(val message: String) : Result()
        data class Failure(val message: String) : Result()
    }

    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        birth: LocalDate?,
        country: String,
        termsAccepted: Boolean
    ): Result {
        if (name.isBlank()) {
            return Result.NameError("El nombre no puede estar vacío")
        }

        if (!InputValidator.isValidEmail(email)) {
            return Result.EmailError("Ingresa un correo válido")
        }

        if (!InputValidator.isValidPassword(password)) {
            return Result.PasswordError("Contraseña débil (mínimo 6 caracteres)")
        }

        if (country.isBlank()) {
            return Result.CountryError("Seleccioná un país de residencia")
        }

        if (birth == null) {
            return Result.BirthDateError("Ingresa una fecha de nacimiento")
        }

        if (!birth.isBefore(LocalDate.now())) {
            return Result.BirthDateError("La fecha de nacimiento no puede ser hoy ni una fecha futura")
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
