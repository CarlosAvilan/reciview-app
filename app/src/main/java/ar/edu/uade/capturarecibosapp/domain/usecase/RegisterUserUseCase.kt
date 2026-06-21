package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository

/**
 * Encapsula la regla de "qué es un registro válido":
 * password mínimo, email con formato básico, términos aceptados.
 */
class RegisterUserUseCase(
    private val authRepository: AuthRepository
) {
    sealed class Result {
        data class Success(val email: String) : Result()
        data class PasswordError(val message: String) : Result()
        data class EmailError(val message: String) : Result()
        data class TermsError(val message: String) : Result()
        data class Failure(val message: String) : Result()
    }

    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        birth: String,
        country: String,
        termsAccepted: Boolean
    ): Result {
        if (password.length < 6) {
            return Result.PasswordError("Contraseña débil (mínimo 6 caracteres)")
        }

        if (email.isEmpty() || !email.contains("@")) {
            return Result.EmailError("Ingresa un correo válido")
        }

        if (!termsAccepted) {
            return Result.TermsError("Debes aceptar los términos y condiciones")
        }

        val result = authRepository.registerUser(
            email = email,
            pass = password,
            name = name,
            birth = birth,
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
