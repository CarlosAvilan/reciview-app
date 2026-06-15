package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TicketsViewModel(application: Application) : AndroidViewModel(application) {
    private val ticketRepository = DependencyProvider.provideTicketRepository(application)
    private val userId = SessionManager.userId ?: ""

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("Todos")
    var isLoading by mutableStateOf(false)

    private val _allTickets = ticketRepository.getTickets(userId)
    
    val filteredTickets: StateFlow<List<Ticket>> = combine(
        _allTickets,
        snapshotFlow { searchQuery },
        snapshotFlow { selectedCategory }
    ) { tickets, query, category ->
        tickets.filter { ticket ->
            val matchesSearch = ticket.establishment.contains(query, ignoreCase = true) || 
                               ticket.createdAt.contains(query, ignoreCase = true)
            val matchesCategory = category == "Todos" || "Otros" == category // TODO: Implement category logic properly if needed
            matchesSearch && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categories = listOf("Todos", "Alimentos", "Combustible", "Salud", "Gastronomía")
    
    var selectedTicket by mutableStateOf<Ticket?>(null)

    init {
        sync()
    }

    fun sync() {
        viewModelScope.launch {
            isLoading = true
            ticketRepository.syncPendingTickets()
            isLoading = false
        }
    }
}

// Helper to convert Compose state to Flow
fun <T> snapshotFlow(block: () -> T): kotlinx.coroutines.flow.Flow<T> = androidx.compose.runtime.snapshotFlow(block)
