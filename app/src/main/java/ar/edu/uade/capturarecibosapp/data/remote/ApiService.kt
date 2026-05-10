package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.model.TicketData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Interfaz para enviar los datos al backend (Ktor)
interface ApiService {
    @POST("tickets")
    suspend fun enviarTicket(@Body ticket: TicketData): Response<Unit>
}