package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.repository.TicketRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Encapsula la regla de "qué es un gasto manual válido":
 * validación de monto/establecimiento/categoría, conversión de
 * fecha UI -> API y armado del Ticket.
 */
class SaveManualExpenseUseCase(
    private val ticketRepository: TicketRepository
) {
    sealed class Result {
        object Success : Result()

        /**
         * Cada campo errado trae su propio mensaje (o null si ese campo
         * estaba bien). Así la UI puede mostrar texto puntual debajo de
         * cada input, no solo el borde en rojo.
         */
        data class ValidationError(
            val montoError: String?,
            val establecimientoError: String?,
            val categoriaError: String?
        ) : Result()

        data class Failure(val message: String) : Result()
    }

    private val uiFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend operator fun invoke(
        montoRaw: String,
        establecimiento: String,
        categoriaNombre: String,
        descripcion: String,
        fechaUi: String,
        userId: String,
        categories: List<UserCategory>,
        photoUrl: String? = null
    ): Result {
        val amount = montoRaw.toFloatOrNull()

        // Mensaje distinto según la razón puntual, no un genérico "campo inválido".
        val montoError = when {
            montoRaw.isBlank() -> "Ingresá un monto"
            amount == null -> "El monto no es un número válido"
            amount <= 0 -> "El monto debe ser mayor a 0"
            else -> null
        }

        val establecimientoError = if (establecimiento.isBlank()) {
            "Ingresá el nombre del establecimiento"
        } else null

        val categoriaError = if (categoriaNombre.isBlank()) {
            "Seleccioná una categoría"
        } else null

        if (montoError != null || establecimientoError != null || categoriaError != null) {
            return Result.ValidationError(montoError, establecimientoError, categoriaError)
        }

        val selectedCat = categories.find { it.name == categoriaNombre }

        val fechaApi = try {
            LocalDate.parse(fechaUi, uiFormatter).format(apiFormatter)
        } catch (e: Exception) {
            fechaUi // fallback, igual que el ViewModel original
        }

        val ticket = Ticket(
            createdAt = fechaApi,
            userId = userId,
            categoryId = selectedCat?.id,
            establishment = establecimiento,
            amount = amount ?: 0f,
            photoUrl = photoUrl,
            description = descripcion,
            syncStatus = SyncStatus.PENDIENTE_AGREGAR
        )

        val result = ticketRepository.saveTicket(ticket)
        return if (result.isSuccess) Result.Success
        else Result.Failure("Error al guardar el gasto")
    }
}