package ar.edu.uade.capturarecibosapp.data.repository

import ar.edu.uade.capturarecibosapp.data.model.TicketData
import ar.edu.uade.capturarecibosapp.data.remote.RetrofitClient

class TicketRepository {
    private val apiService = RetrofitClient.ticketService

    suspend fun getTickets(): Result<List<TicketData>> {
        return try {
            val response = apiService.getTickets()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener tickets"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendTicket(ticket: TicketData): Result<Unit> {
        return try {
            val response = apiService.sendTicket(ticket)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al enviar ticket"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
