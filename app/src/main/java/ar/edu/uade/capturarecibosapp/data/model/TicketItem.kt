package ar.edu.uade.capturarecibosapp.data.model

data class TicketItem(
    val id: Int,
    val commerce: String,
    val date: String,
    val amount: String,
    val category: String = "Otros",
    val imageRes: Int? = null,
    val description: String = ""
)
