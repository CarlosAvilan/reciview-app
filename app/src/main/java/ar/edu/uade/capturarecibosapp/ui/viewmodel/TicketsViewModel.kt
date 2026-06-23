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
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import kotlinx.coroutines.launch

class TicketsViewModel(application: Application) : AndroidViewModel(application) {
    private val ticketRepository = DependencyProvider.provideTicketRepository(application)
    private val categoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val userId = SessionManager.userId ?: ""

    // Estados de Compose para reactividad inmediata
    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("Todos")
    var isLoading by mutableStateOf(false)
    var selectedTicket by mutableStateOf<Ticket?>(null)
    
    // Listas observables por Compose mediante mutableStateOf
    private var allTickets by mutableStateOf<List<Ticket>>(emptyList())

    var categoryList by mutableStateOf<List<UserCategory>>(emptyList())
        private set

    var categoryNames by mutableStateOf<List<String>>(listOf("Todos"))
        private set

    // Lista filtrada calculada reactivamente
    val filteredTickets: List<Ticket>
        get() = allTickets.filter { ticket ->
            val matchesSearch = ticket.establishment.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = if (selectedCategory == "Todos") {
                true
            } else {
                val cat = categoryList.find { it.name == selectedCategory }
                cat?.id == ticket.categoryId
            }
            
            matchesSearch && matchesCategory
        }

    init {
        // Observar categorías de Room
        viewModelScope.launch {
            categoryRepository.getCategories(userId).collect { cats ->
                categoryList = cats
                categoryNames = listOf("Todos") + cats.map { it.name }
            }
        }

        // Observar tickets de Room
        viewModelScope.launch {
            ticketRepository.getTickets(userId).collect { tickets ->
                allTickets = tickets
            }
        }

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
