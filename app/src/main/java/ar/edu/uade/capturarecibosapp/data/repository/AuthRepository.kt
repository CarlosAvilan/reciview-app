package ar.edu.uade.capturarecibosapp.data.repository

import kotlinx.coroutines.delay

class AuthRepository {

    suspend fun sendRecoveryCode(email: String): Result<Unit> {
        // Mocking network delay
        delay(1000)
        return if (email.contains("@")) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Email inválido"))
        }
    }

    suspend fun resetPassword(email: String, code: String, newPass: String): Result<Unit> {
        // Mocking network delay
        delay(1000)
        return if (code.length == 6) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Código inválido"))
        }
    }
}
