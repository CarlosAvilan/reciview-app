package ar.edu.uade.capturarecibosapp.data.model

data class TicketData(
    val comercio: String,
    val total: Double,
    val fecha: String,
    val descripcion: String = ""
)
