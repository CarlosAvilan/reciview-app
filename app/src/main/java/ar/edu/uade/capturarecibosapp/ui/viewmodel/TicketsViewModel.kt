package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class TicketItem(
    val commerce: String,
    val date: String,
    val amount: String,
    val category: String = "Otros"
)

class TicketsViewModel : ViewModel() {
    var searchQuery by mutableStateOf("")
    
    var selectedCategory by mutableStateOf("Todos")

    val tickets = listOf(
        TicketItem("Coto Digital", "10 Mayo", "$15.400"),
        TicketItem("Farmacity", "08 Mayo", "$3.200"),
        TicketItem("YPF", "05 Mayo", "$12.000"),
        TicketItem("Starbucks", "03 Mayo", "$4.500")
    )

    val categories = listOf("Todos", "Garantías", "Alimentos", "Servicios")
}
