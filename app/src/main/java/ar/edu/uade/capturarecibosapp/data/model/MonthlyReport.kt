package ar.edu.uade.capturarecibosapp.data.model

data class MonthlyReport(
    val month: String,
    val amount: Float,
    val averageCost: String,
    val mostActiveDay: String
)