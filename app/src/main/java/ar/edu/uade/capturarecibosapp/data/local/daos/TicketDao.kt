package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.*
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.model.TicketItem
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    // Cabecera del Ticket
    @Query("SELECT * FROM tickets WHERE user_id = :userId AND sync_status != 'PENDIENTE_ELIMINACION' ORDER BY created_at DESC")
    fun getTicketsForUser(userId: String): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE sync_status != 'ACTUALIZADO'")
    suspend fun getPendingSyncTickets(): List<Ticket>

    @Query("SELECT * FROM tickets WHERE id = :ticketId")
    suspend fun getTicketById(ticketId: Long): Ticket?

    @Query("SELECT * FROM tickets WHERE remote_id = :remoteId")
    suspend fun getTicketByRemoteId(remoteId: Long): Ticket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket): Long

    @Update
    suspend fun updateTicket(ticket: Ticket)

    @Delete
    suspend fun deleteTicketPhysically(ticket: Ticket)

    @Query("UPDATE tickets SET sync_status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: SyncStatus)

    @Query("UPDATE tickets SET remote_id = :remoteId, sync_status = 'ACTUALIZADO' WHERE id = :localId")
    suspend fun updateRemoteIdAfterSync(localId: Long, remoteId: Long)

    @Query("SELECT * FROM items ORDER BY id DESC")
    fun getAllTicketItems(): Flow<List<TicketItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<Ticket>)

    // Ítems individuales desglosados del Ticket
    @Query("SELECT * FROM items WHERE ticket_id = :ticketId")
    fun getItemsForTicket(ticketId: Long): Flow<List<TicketItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicketItems(items: List<TicketItem>)

    // Categorías de gastos del usuario (redundante si ya está en CategoryDao, pero lo dejamos por ahora si se usa aquí)
    @Query("SELECT * FROM user_categories WHERE user_id = :userId OR user_id IS NULL")
    fun getCategoriesForUser(userId: String): Flow<List<UserCategory>>
}
