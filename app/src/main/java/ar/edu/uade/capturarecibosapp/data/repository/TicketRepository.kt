package ar.edu.uade.capturarecibosapp.data.repository

import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.local.daos.CategoryDao
import ar.edu.uade.capturarecibosapp.data.local.daos.TicketDao
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.remote.TicketApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.TicketDTO
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class TicketRepository(
    private val ticketDao: TicketDao,
    private val categoryDao: CategoryDao,
    private val apiService: TicketApiService
) {

    fun getTickets(userId: String): Flow<List<Ticket>> =
        ticketDao.getTicketsForUser(userId)

    suspend fun saveTicket(ticket: Ticket): Result<Unit> {
        return try {
            if (ticket.id == 0L) {
                ticketDao.insertTicket(ticket.copy(syncStatus = SyncStatus.PENDIENTE_AGREGAR))
            } else {
                ticketDao.updateTicket(ticket.copy(syncStatus = SyncStatus.PENDIENTE_CAMBIO))
            }
            syncPendingTickets()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncPendingTickets(): Result<Unit> {
        return try {
            val userId = SessionManager.userId ?: return Result.failure(Exception("No session"))
            
            // 1. Bajada: Mapear IDs remotos de categorías a IDs locales
            val remoteResponse = apiService.getTickets("eq.$userId")
            if (remoteResponse.isSuccessful) {
                remoteResponse.body()?.forEach { dto ->
                    val remoteId = dto.id ?: return@forEach
                    val localCatId = dto.categoryId?.let { rId -> 
                        categoryDao.getCategoryByRemoteId(rId)?.id 
                    }

                    val existing = ticketDao.getTicketByRemoteId(remoteId)
                    if (existing == null) {
                        ticketDao.insertTicket(Ticket(
                            createdAt = dto.createdAt,
                            userId = dto.userId,
                            categoryId = localCatId,
                            establishment = dto.establishment,
                            amount = dto.amount,
                            photoUrl = dto.photoUrl,
                            description = dto.description,
                            remoteId = remoteId,
                            syncStatus = SyncStatus.ACTUALIZADO
                        ))
                    } else if (existing.syncStatus == SyncStatus.ACTUALIZADO) {
                        ticketDao.updateTicket(existing.copy(categoryId = localCatId))
                    }
                }
            }

            // 2. Subida: Validar que la categoría ya exista en el servidor
            val pending = ticketDao.getPendingSyncTickets()
            pending.forEach { local ->
                val remoteCatId = if (local.categoryId != null) {
                    val cat = categoryDao.getCategoryById(local.categoryId)
                    // SI LA CATEGORÍA ES OFFLINE Y NO SE SINCRONIZÓ, ESPERAMOS.
                    if (cat?.remoteId == null) return@forEach 
                    cat.remoteId
                } else null

                when (local.syncStatus) {
                    SyncStatus.PENDIENTE_AGREGAR -> {
                        val response = apiService.createTicket(local.toDTO(remoteCatId))
                        if (response.isSuccessful) {
                            response.body()?.firstOrNull()?.id?.let {
                                ticketDao.updateRemoteIdAfterSync(local.id, it)
                            }
                        }
                    }
                    SyncStatus.PENDIENTE_CAMBIO -> {
                        local.remoteId?.let { rId ->
                            val response = apiService.updateTicket("eq.$rId", mapOf("category_id" to remoteCatId))
                            if (response.isSuccessful) ticketDao.updateSyncStatus(local.id, SyncStatus.ACTUALIZADO)
                        }
                    }
                    SyncStatus.PENDIENTE_ELIMINACION -> {
                        local.remoteId?.let { rId ->
                            if (apiService.deleteTicket("eq.$rId").isSuccessful) ticketDao.deleteTicketPhysically(local)
                        } ?: ticketDao.deleteTicketPhysically(local)
                    }
                    else -> {}
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Ticket.toDTO(remoteCatId: Long?) = TicketDTO(
        id = remoteId,
        createdAt = createdAt,
        userId = userId,
        categoryId = remoteCatId,
        establishment = establishment,
        amount = amount,
        photoUrl = photoUrl,
        description = description
    )
}
