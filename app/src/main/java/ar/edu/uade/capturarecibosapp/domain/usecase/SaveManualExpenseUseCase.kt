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
        data class ValidationError(
            val montoError: Boolean,
            val establecimientoError: Boolean,
            val categoriaError: Boolean
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
        categories: List<UserCategory>
    ): Result {
        val amount = montoRaw.toFloatOrNull()

        val montoError = amount == null || amount <= 0
        val establecimientoError = establecimiento.isBlank()
        val categoriaError = categoriaNombre.isBlank()

        if (montoError || establecimientoError || categoriaError) {
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
            photoUrl = null,
            description = descripcion,
            syncStatus = SyncStatus.PENDIENTE_AGREGAR
        )

        val result = ticketRepository.saveTicket(ticket)
        return if (result.isSuccess) Result.Success
        else Result.Failure("Error al guardar el gasto")
    }
}
