package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.data.remote.dto.TicketDTO
import retrofit2.Response
import retrofit2.http.*

interface TicketApiService {
    @GET("rest/v1/tickets")
    suspend fun getTickets(
        @Query("user_id") userIdFilter: String,
        @Query("select") select: String = "*"
    ): Response<List<TicketDTO>>

    @POST("rest/v1/tickets")
    suspend fun createTicket(
        @Body ticket: TicketDTO
    ): Response<List<TicketDTO>>

    @PATCH("rest/v1/tickets")
    suspend fun updateTicket(
        @Query("id") idFilter: String,
        @Body ticket: Map<String, Any?>
    ): Response<Unit>

    @DELETE("rest/v1/tickets")
    suspend fun deleteTicket(
        @Query("id") idFilter: String
    ): Response<Unit>

    @DELETE("rest/v1/tickets")
    suspend fun deleteTicketsByUserId(
        @Query("user_id") userIdFilter: String
    ): Response<Unit>
}
