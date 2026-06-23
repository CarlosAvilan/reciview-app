package ar.edu.uade.capturarecibosapp.domain.usecase
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository

class ChangePasswordUseCase(
    private val repository: AuthRepository
) {
    sealed class Result {
        object Success : Result()
        data class ValidationError(
            val oldPasswordError: Boolean = false,
            val newPasswordError: Boolean = false,
            val confirmPasswordError: Boolean = false,
            val message: String
        ) : Result()
        data class Failure(val message: String) : Result()
    }

    suspend operator fun invoke(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result {
        if (oldPassword.isBlank()) {
            return Result.ValidationError(
                oldPasswordError = true,
                message = "La contraseña actual no puede estar vacía"
            )
        }

        if (newPassword.isBlank()) {
            return Result.ValidationError(
                newPasswordError = true,
                message = "La nueva contraseña no puede estar vacía"
            )
        }

        if (newPassword.length < 6) {
            return Result.ValidationError(
                newPasswordError = true,
                message = "La nueva contraseña debe tener al menos 6 caracteres"
            )
        }

        if (newPassword != confirmPassword) {
            return Result.ValidationError(
                confirmPasswordError = true,
                message = "Las contraseñas no coinciden"
            )
        }

        val email = SessionManager.userEmail ?: return Result.Failure("Usuario no autenticado")

        // Validar que la contraseña anterior sea correcta re-autenticando
        val loginResult = repository.login(email, oldPassword)
        if (loginResult.isFailure) {
            return Result.ValidationError(
                oldPasswordError = true,
                message = "La contraseña actual es incorrecta"
            )
        }

        // Si todo es correcto, cambiar la contraseña
        val changeResult = repository.changePassword(newPassword)
        return if (changeResult.isSuccess) {
            Result.Success
        } else {
            Result.Failure("Error al cambiar la contraseña")
        }
    }
}
