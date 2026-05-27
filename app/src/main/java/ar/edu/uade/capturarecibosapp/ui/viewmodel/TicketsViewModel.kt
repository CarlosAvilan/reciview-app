package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.R
import ar.edu.uade.capturarecibosapp.data.model.TicketItem

class TicketsViewModel : ViewModel() {
    var searchQuery by mutableStateOf("")
    
    var selectedCategory by mutableStateOf("Todos")

    private val allTickets = listOf(
        TicketItem(
            id = 1,
            commerce = "Shell", 
            date = "25 Mayo", 
            amount = "$9.346,97", 
            category = "Combustible", 
            imageRes = R.drawable.ticket_shell,
            description = "Shell V-Power Nafta - Pago Efectivo"
        ),
        TicketItem(id = 2, commerce = "Coto Digital", date = "10 Mayo", amount = "$15.400", category = "Alimentos"),
        TicketItem(id = 3, commerce = "Farmacity", date = "08 Mayo", amount = "$3.200", category = "Salud"),
        TicketItem(id = 4, commerce = "YPF", date = "05 Mayo", amount = "$12.000", category = "Combustible"),
        TicketItem(id = 5, commerce = "Starbucks", date = "03 Mayo", amount = "$4.500", category = "Gastronomía")
    )

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
}
