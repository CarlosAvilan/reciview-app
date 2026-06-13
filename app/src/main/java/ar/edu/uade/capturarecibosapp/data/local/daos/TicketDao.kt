package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.model.TicketItem
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    // Cabecera del Ticket
    @Query("SELECT * FROM tickets ORDER BY created_at DESC")
    fun getAllTickets(): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE id = :ticketId")
    suspend fun getTicketById(ticketId: Long): Ticket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket): Long // Devuelve el ID autogenerado

    @Delete
    suspend fun deleteTicket(ticket: Ticket)

    @Query("SELECT * FROM items ORDER BY id DESC")
    fun getAllTicketItems(): Flow<List<TicketItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<Ticket>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleTicket(ticket: Ticket): Long

    // Ítems individuales desglosados del Ticket
    @Query("SELECT * FROM items WHERE ticket_id = :ticketId")
    fun getItemsForTicket(ticketId: Long): Flow<List<TicketItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicketItems(items: List<TicketItem>)

    // Categorías de gastos del usuario
    @Query("SELECT * FROM user_categories WHERE user_id = :userId OR user_id IS NULL")
    fun getCategoriesForUser(userId: String): Flow<List<UserCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: UserCategory)
}