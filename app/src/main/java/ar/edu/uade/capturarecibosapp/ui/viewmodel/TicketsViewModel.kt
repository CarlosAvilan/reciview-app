package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.model.TicketItem
import ar.edu.uade.capturarecibosapp.data.repository.TicketRepository
import kotlinx.coroutines.launch

class TicketsViewModel : ViewModel() {
    private val ticketRepository = DependencyProvider.provideTicketRepository()

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("Todos")
    var isLoading by mutableStateOf(false)

    private val allTickets = mutableStateListOf<TicketItem>()

    val filteredTickets by derivedStateOf {
        allTickets.filter { ticket ->
            val matchesSearch = ticket.commerce.contains(searchQuery, ignoreCase = true) || 
                               ticket.date.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "Todos" || ticket.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    val categories = listOf("Todos", "Alimentos", "Combustible", "Salud", "Gastronomía")
    
    var selectedTicket by mutableStateOf<TicketItem?>(null)

    init {
        loadTickets()
    }

    private fun loadTickets() {
        isLoading = true
        viewModelScope.launch {
            val result = ticketRepository.getTickets()
            isLoading = false
            if (result.isSuccess) {
                allTickets.clear()
                // Mapear TicketData (DTO) a TicketItem (UI Model) si es necesario
                // Por ahora asumimos que TicketData se puede convertir o se usa TicketItem
                // allTickets.addAll(result.getOrDefault(emptyList()))
            }
        }
    }
}
