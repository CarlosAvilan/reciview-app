package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.model.Ticket
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TicketApiService {
    @GET("rest/v1/tickets")
    suspend fun getTickets(): Response<List<Ticket>>

    @POST("rest/v1/tickets")
    suspend fun sendTicket(@Body ticket: Ticket): Response<Unit>
}
