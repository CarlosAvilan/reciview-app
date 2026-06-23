package ar.edu.uade.capturarecibosapp.data.repository

import android.util.Log
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.local.daos.CategoryDao
import ar.edu.uade.capturarecibosapp.data.local.daos.TicketDao
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.remote.TicketApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.TicketDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class TicketRepository(
    private val ticketDao: TicketDao,
    private val categoryDao: CategoryDao,
    private val apiService: TicketApiService,
) {

    private val tag = "TicketRepository"

    fun getTickets(userId: String): Flow<List<Ticket>> =
        ticketDao.getTicketsForUser(userId)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTicketsWithCategories(userId: String): Flow<List<Pair<Ticket, UserCategory?>>> {
        return ticketDao.getTicketsForUser(userId).flatMapLatest { tickets ->
            if (tickets.isEmpty()) return@flatMapLatest flowOf(emptyList())
            
            val categoryFlows = tickets.map { ticket ->
                if (ticket.categoryId != null) {
                    categoryDao.getCategoryByIdFlow(ticket.categoryId).map { ticket to it }
                } else {
                    flowOf(ticket to null)
                }
            }
            combine(categoryFlows) { it.toList() }
        }
    }

    suspend fun saveTicket(ticket: Ticket): Result<Unit> {
        return try {
            Log.d(tag, "Saving ticket: ${ticket.establishment}")
            if (ticket.id == 0L) {
                ticketDao.insertTicket(ticket.copy(syncStatus = SyncStatus.PENDIENTE_AGREGAR))
            } else {
                ticketDao.updateTicket(ticket.copy(syncStatus = SyncStatus.PENDIENTE_CAMBIO))
            }
            syncPendingTickets()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error saving ticket", e)
            Result.failure(e)
        }
    }

    suspend fun syncPendingTickets(): Result<Unit> {
        Log.d(tag, "Starting syncPendingTickets")
        return try {
            val userId = SessionManager.userId ?: return Result.failure(Exception("No session"))
            
            // 1. Bajada: Mapear IDs remotos de categorías a IDs locales
            Log.d(tag, "Pulling tickets for user: $userId")
            val remoteResponse = apiService.getTickets("eq.$userId")
            if (remoteResponse.isSuccessful) {
                val remoteItems = remoteResponse.body() ?: emptyList()
                Log.d(tag, "Pulled ${remoteItems.size} tickets from remote")
                remoteItems.forEach { dto ->
                    val remoteId = dto.id ?: return@forEach
                    val localCatId = dto.categoryId?.let { rId -> 
                        categoryDao.getCategoryByRemoteId(rId)?.id 
                    }

                    val existing = ticketDao.getTicketByRemoteId(remoteId)
                    if (existing == null) {
                        Log.d(tag, "Inserting new remote ticket: ${dto.establishment}")
                        ticketDao.insertTicket(
                            Ticket(
                                createdAt = dto.createdAt.substringBefore('T'),
                                userId = dto.userId,
                                categoryId = localCatId,
                                establishment = dto.establishment,
                                amount = dto.amount,
                                photoUrl = dto.photoUrl,
                                description = dto.description,
                                remoteId = remoteId,
                                syncStatus = SyncStatus.ACTUALIZADO,
                            )
                        )
                    } else if (existing.syncStatus == SyncStatus.ACTUALIZADO) {
                        Log.d(tag, "Updating existing remote ticket: ${dto.establishment}")
                        ticketDao.updateTicket(existing.copy(
                            createdAt = dto.createdAt.substringBefore('T'),
                            establishment = dto.establishment,
                            amount = dto.amount,
                            photoUrl = dto.photoUrl,
                            description = dto.description,
                            categoryId = localCatId
                        ))
                    }
                }
            } else {
                Log.e(tag, "Failed to pull tickets: ${remoteResponse.code()}")
            }

            // 2. Subida: Validar que la categoría ya exista en el servidor
            val pending = ticketDao.getPendingSyncTickets()
            Log.d(tag, "Found ${pending.size} pending tickets to push")
            pending.forEach { local ->
                val remoteCatId = if (local.categoryId != null) {
                    val cat = categoryDao.getCategoryById(local.categoryId)
                    // SI LA CATEGORÍA ES OFFLINE Y NO SE SINCRONIZÓ, ESPERAMOS.
                    if (cat?.remoteId == null) {
                        Log.d(tag, "Ticket '${local.establishment}' waiting for category '${cat?.name}' to sync")
                        return@forEach 
                    }
                    cat.remoteId
                } else null

                when (local.syncStatus) {
                    SyncStatus.PENDIENTE_AGREGAR -> {
                        Log.d(tag, "Pushing new ticket: ${local.establishment}")
                        val response = apiService.createTicket(local.toDTO(remoteCatId))
                        if (response.isSuccessful) {
                            val remote = response.body()?.firstOrNull()
                            if (remote?.id != null) {
                                Log.d(tag, "Ticket '${local.establishment}' synced successfully with remoteId ${remote.id}")
                                ticketDao.updateRemoteIdAfterSync(local.id, remote.id)
                            } else {
                                Log.w(tag, "Ticket creation successful but body was empty or missing ID")
                            }
                        } else {
                            Log.e(tag, "Error creating ticket '${local.establishment}': ${response.code()}")
                        }
                    }
                    SyncStatus.PENDIENTE_CAMBIO -> {
                        Log.d(tag, "Pushing update for ticket: ${local.establishment}")
                        local.remoteId?.let { rId ->
                            val updateMap = mapOf(
                                "category_id" to remoteCatId,
                                "establishment" to local.establishment,
                                "amount" to local.amount,
                                "description" to local.description,
                                "created_at" to local.createdAt
                            )
                            val response = apiService.updateTicket("eq.$rId", updateMap)
                            if (response.isSuccessful) {
                                Log.d(tag, "Ticket '${local.establishment}' updated successfully")
                                ticketDao.updateSyncStatus(local.id, SyncStatus.ACTUALIZADO)
                            } else {
                                Log.e(tag, "Error updating ticket '${local.establishment}': ${response.code()}")
                            }
                        }
                    }
                    SyncStatus.PENDIENTE_ELIMINACION -> {
                        Log.d(tag, "Pushing deletion for ticket: ${local.establishment}")
                        local.remoteId?.let { rId ->
                            if (apiService.deleteTicket("eq.$rId").isSuccessful) {
                                Log.d(tag, "Ticket '${local.establishment}' deleted remotely")
                                ticketDao.deleteTicketPhysically(local)
                            } else {
                                Log.e(tag, "Error deleting ticket remotely")
                            }
                        } ?: ticketDao.deleteTicketPhysically(local)
                    }
                    else -> {}
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Unexpected error during sync", e)
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
