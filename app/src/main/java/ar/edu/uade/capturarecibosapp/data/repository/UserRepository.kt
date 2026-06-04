package ar.edu.uade.capturarecibosapp.data.repository

import kotlinx.coroutines.delay

class UserRepository {
    // Simulamos almacenamiento local/remoto
    private var budget = "60.000,00"

    suspend fun getBudget(): String {
        delay(500)
        return budget
    }

    suspend fun updateBudget(newBudget: String): Result<Unit> {
        delay(1000)
        // Validamos que sea un número (simplificado)
        return if (newBudget.isNotBlank()) {
            budget = newBudget
            Result.success(Unit)
        } else {
            Result.failure(Exception("El presupuesto no puede estar vacío"))
        }
    }
}
