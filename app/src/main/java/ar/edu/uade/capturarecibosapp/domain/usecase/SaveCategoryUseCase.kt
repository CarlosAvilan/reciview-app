package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.repository.CategoryRepository

/**
 * Encapsula la regla de negocio de "qué es una categoría válida":
 * parseo de monto en formato local ($1.234,56), validaciones y
 * decisión de crear vs. actualizar.
 */
class SaveCategoryUseCase(
    private val repository: CategoryRepository
) {
    sealed class Result {
        data class Success(val category: UserCategory) : Result()
        data class ValidationError(
            val nameError: Boolean,
            val budgetError: Boolean,
            val message: String
        ) : Result()
        data class Failure(val message: String) : Result()
    }

    suspend operator fun invoke(
        nombre: String,
        limiteRaw: String,
        icon: String,
        userId: String,
        existingCategory: UserCategory? = null
    ): Result {
        if (nombre.isBlank()) {
            return Result.ValidationError(
                nameError = true,
                budgetError = false,
                message = "El nombre no puede estar vacío"
            )
        }

        val budget = parseMonto(limiteRaw)
        if (budget == null) {
            return Result.ValidationError(
                nameError = false,
                budgetError = true,
                message = "Ingresa un monto válido"
            )
        }

        if (budget < 0) {
            return Result.ValidationError(
                nameError = false,
                budgetError = true,
                message = "El presupuesto no puede ser negativo"
            )
        }

        val category = existingCategory?.copy(
            name = nombre,
            icon = icon,
            budget = budget
        ) ?: UserCategory(
            name = nombre,
            icon = icon,
            budget = budget,
            userId = userId
        )

        val result = repository.saveCategory(category)
        return if (result.isSuccess) {
            Result.Success(category)
        } else {
            Result.Failure("Error al guardar la categoría")
        }
    }

    /**
     * Convierte "$1.234,56" (formato AR) a 1234.56
     */
    private fun parseMonto(raw: String): Double? {
        val clean = raw.replace("$", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
        return clean.toDoubleOrNull()
    }
}
