package ar.edu.uade.capturarecibosapp.data.local.seeders

import ar.edu.uade.capturarecibosapp.R
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.model.TicketItem

class TicketSeeder {
    fun provideInitialTickets(): List<Ticket> = listOf(
        Ticket(id = 1, createdAt = "25 Mayo", userId = UserSeeder.MOCK_USER_ID, categoryId = 2, establishment = "Shell", amount = 9346.97f, photoUrl = null, description = "Shell V-Power Nafta"),
        Ticket(id = 2, createdAt = "10 Mayo", userId = UserSeeder.MOCK_USER_ID, categoryId = 1, establishment = "Coto Digital", amount = 15400f, photoUrl = null, description = ""),
        Ticket(id = 3, createdAt = "08 Mayo", userId = UserSeeder.MOCK_USER_ID, categoryId = 3, establishment = "Farmacity", amount = 3200f, photoUrl = null, description = ""),
        Ticket(id = 4, createdAt = "05 Mayo", userId = UserSeeder.MOCK_USER_ID, categoryId = 2, establishment = "YPF", amount = 12000f, photoUrl = null, description = ""),
        Ticket(id = 5, createdAt = "03 Mayo", userId = UserSeeder.MOCK_USER_ID, categoryId = 4, establishment = "Starbucks", amount = 4500f, photoUrl = null, description = "")
    )

    fun provideInitialTicketItems(): List<TicketItem> = listOf(
        TicketItem(id = 1, ticketId = 1, commerce = "Shell", date = "25 Mayo", amount = 9346.97f, category = "Combustible", imageRes = R.drawable.ticket_shell, description = "Shell V-Power Nafta - Pago Efectivo"),
        TicketItem(id = 2, ticketId = 2, commerce = "Coto Digital", date = "10 Mayo", amount = 15400f, category = "Alimentos"),
        TicketItem(id = 3, ticketId = 3, commerce = "Farmacity", date = "08 Mayo", amount = 3200f, category = "Salud"),
        TicketItem(id = 4, ticketId = 4, commerce = "YPF", date = "05 Mayo", amount = 12000f, category = "Combustible"),
        TicketItem(id = 5, ticketId = 5, commerce = "Starbucks", date = "03 Mayo", amount = 4500f, category = "Gastronomía")
    )
}