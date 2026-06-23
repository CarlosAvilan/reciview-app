package ar.edu.uade.capturarecibosapp.domain.usecase

class ChangePasswordUseCase {

    sealed class Result {
        object Success : Result()
        data class ValidationError(
            val currentPasswordError: String?,
            val newPasswordError: String?,
            val repeatPasswordError: String?
        ) : Result()
    }

    operator fun invoke(
        currentPassword: String,
        newPassword: String,
        repeatPassword: String
    ): Result {
        val currentPasswordError =
            if (currentPassword.isBlank()) "Ingresá la contraseña actual" else null

        val newPasswordError = when {
            newPassword.isBlank() -> "Ingresá la nueva contraseña"
            newPassword.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }

        // Solo validamos coincidencia si la nueva contraseña es formalmente válida
        val repeatPasswordError = when {
            repeatPassword.isBlank() -> "Repetí la nueva contraseña"
            newPasswordError == null && newPassword != repeatPassword -> "Las contraseñas no coinciden"
            else -> null
        }

        return if (currentPasswordError != null || newPasswordError != null || repeatPasswordError != null) {
            Result.ValidationError(currentPasswordError, newPasswordError, repeatPasswordError)
        } else {
            Result.Success
        }
    }
}
