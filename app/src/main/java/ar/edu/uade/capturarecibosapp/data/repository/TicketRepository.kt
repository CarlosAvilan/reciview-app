package ar.edu.uade.capturarecibosapp.data.repository

import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.local.daos.TicketDao
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.remote.TicketApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.TicketDTO
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class TicketRepository(
    private val ticketDao: TicketDao,
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
            // Disparar sincronización de fondo
            syncPendingTickets()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTicket(ticket: Ticket): Result<Unit> {
        return try {
            if (ticket.remoteId == null) {
                // Si nunca se sincronizó, borrar directo
                ticketDao.deleteTicketPhysically(ticket)
            } else {
                // Borrado lógico para sincronizar luego
                ticketDao.updateSyncStatus(ticket.id, SyncStatus.PENDIENTE_ELIMINACION)
            }
            syncPendingTickets()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncPendingTickets(): Result<Unit> {
        return try {
            val userId = SessionManager.userId
            if (userId != null) {
                // 1. Sincronización de bajada
                val remoteResponse = apiService.getTickets()
                if (remoteResponse.isSuccessful) {
                    remoteResponse.body()?.forEach { dto ->
                        val remoteId = dto.id ?: return@forEach
                        val existing = ticketDao.getTicketByRemoteId(remoteId)
                        if (existing == null) {
                            val newTicket = Ticket(
                                createdAt = dto.createdAt,
                                userId = dto.userId,
                                categoryId = dto.categoryId,
                                establishment = dto.establishment,
                                amount = dto.amount,
                                photoUrl = dto.photoUrl,
                                description = dto.description,
                                remoteId = remoteId,
                                syncStatus = SyncStatus.ACTUALIZADO
                            )
                            ticketDao.insertTicket(newTicket)
                        } else if (existing.syncStatus == SyncStatus.ACTUALIZADO) {
                            ticketDao.updateTicket(existing.copy(
                                createdAt = dto.createdAt,
                                categoryId = dto.categoryId,
                                establishment = dto.establishment,
                                amount = dto.amount,
                                photoUrl = dto.photoUrl,
                                description = dto.description
                            ))
                        }
                    }
                }
            }

            // 2. Sincronización de subida
            val pending = ticketDao.getPendingSyncTickets()
            pending.forEach { local ->
                when (local.syncStatus) {
                    SyncStatus.PENDIENTE_AGREGAR -> {
                        val dto = local.toDTO()
                        val response = apiService.createTicket(dto)
                        if (response.isSuccessful) {
                            val remote = response.body()?.firstOrNull()
                            if (remote?.id != null) {
                                ticketDao.updateRemoteIdAfterSync(local.id, remote.id)
                            }
                        }
                    }
                    SyncStatus.PENDIENTE_CAMBIO -> {
                        local.remoteId?.let { remoteId ->
                            val updateMap = mapOf(
                                "establishment" to local.establishment,
                                "amount" to local.amount,
                                "category_id" to local.categoryId,
                                "description" to local.description,
                                "photo_url" to local.photoUrl,
                                "created_at" to local.createdAt
                            )
                            val response = apiService.updateTicket("eq.$remoteId", updateMap)
                            if (response.isSuccessful) {
                                ticketDao.updateSyncStatus(local.id, SyncStatus.ACTUALIZADO)
                            }
                        }
                    }
                    SyncStatus.PENDIENTE_ELIMINACION -> {
                        local.remoteId?.let { remoteId ->
                            val response = apiService.deleteTicket("eq.$remoteId")
                            if (response.isSuccessful || response.code() == 404) {
                                ticketDao.deleteTicketPhysically(local)
                            }
                        } ?: run {
                            ticketDao.deleteTicketPhysically(local)
                        }
                    }
                    else -> {}
                }
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Ticket.toDTO() = TicketDTO(
        id = remoteId,
        createdAt = createdAt,
        userId = userId,
        categoryId = categoryId,
        establishment = establishment,
        amount = amount,
        photoUrl = photoUrl,
        description = description
    )
}
