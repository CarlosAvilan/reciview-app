package ar.edu.uade.capturarecibosapp.domain

import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object ReportCalculator {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val monthLabels = mapOf(
        1 to "ENE", 2 to "FEB", 3 to "MAR", 4 to "ABR",
        5 to "MAY", 6 to "JUN", 7 to "JUL", 8 to "AGO",
        9 to "SEP", 10 to "OCT", 11 to "NOV", 12 to "DIC"
    )

    private val dayNames = mapOf(
        1 to "Lunes", 2 to "Martes", 3 to "Miércoles",
        4 to "Jueves", 5 to "Viernes", 6 to "Sábado", 7 to "Domingo"
    )

    private data class Entry(val date: LocalDate, val amount: Double)

    fun calcular(tickets: List<Ticket>, expenses: List<ExpenseItem>): List<MonthlyReport> {
        val entries = mutableListOf<Entry>()

        tickets.forEach { ticket ->
            parseDate(ticket.createdAt)?.let { entries.add(Entry(it, ticket.amount.toDouble())) }
        }
        expenses.forEach { expense ->
            parseDate(expense.date)?.let { entries.add(Entry(it, expense.amount)) }
        }

        return entries
            .groupBy { Pair(it.date.year, it.date.monthValue) }
            .entries
            .sortedWith(compareBy({ it.key.first }, { it.key.second }))
            .map { (yearMonth, monthEntries) ->
                val total = monthEntries.sumOf { it.amount }
                val average = total / monthEntries.size
                val mostActiveDay = monthEntries
                    .groupBy { it.date.dayOfWeek.value }
                    .maxByOrNull { it.value.size }
                    ?.key
                    .let { dayNames[it] ?: "-" }

                MonthlyReport(
                    month = monthLabels[yearMonth.second] ?: "",
                    amount = total.toFloat(),
                    averageCost = "$${formatMonto(average)}",
                    mostActiveDay = mostActiveDay
                )
            }
    }

    private fun parseDate(dateStr: String): LocalDate? = try {
        LocalDate.parse(dateStr, dateFormatter)
    } catch (e: Exception) {
        null
    }

    private fun formatMonto(amount: Double): String =
        String.format(Locale.US, "%,.0f", amount).replace(",", ".")
}