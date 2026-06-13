package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.R
import ar.edu.uade.capturarecibosapp.data.model.TicketItem

class WelcomeViewModel : ViewModel() {
    var userName by mutableStateOf("Juan")
        private set

    var totalSpent by mutableStateOf("$45.280,50")
        private set

    var budgetPercentage by mutableStateOf(0.75f)
        private set

    var recentTickets by mutableStateOf(
        listOf(
            TicketItem(
                id = 101,
                ticketId = 101,
                commerce = "Carrefour",
                date = "Hoy, 18:30",
                amount = 4200f,
                category = "Alimentos",
                description = "Compra semanal de supermercado"
            ),
            TicketItem(
                id = 1,
                ticketId = 1,
                commerce = "Shell",
                date = "Ayer, 10:15",
                amount = 15000f,
                category = "Combustible",
                imageRes = R.drawable.ticket_shell,
                description = "Carga de combustible V-Power"
            ),
            TicketItem(
                id = 102,
                ticketId = 102,
                commerce = "Starbucks",
                date = "08/05, 09:00",
                amount = 3500f,
                category = "Gastronomía",
                description = "Café Latte y Muffin"
            )
        )
    )
        private set
}
