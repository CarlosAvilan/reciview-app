package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.R
import ar.edu.uade.capturarecibosapp.data.local.seeders.ExpenseSeeder
import ar.edu.uade.capturarecibosapp.data.local.seeders.TicketSeeder
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import ar.edu.uade.capturarecibosapp.data.model.TicketItem
import ar.edu.uade.capturarecibosapp.domain.ReportCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

class WelcomeViewModel : ViewModel() {

    private val reports = ReportCalculator.calcular(
        tickets = TicketSeeder().provideInitialTickets(),
        expenses = ExpenseSeeder().provideInitialExpenses()
    )

    private val now = LocalDate.now()

    private val monthAbbr = mapOf(
        1 to "ENE", 2 to "FEB", 3 to "MAR", 4 to "ABR",
        5 to "MAY", 6 to "JUN", 7 to "JUL", 8 to "AGO",
        9 to "SEP", 10 to "OCT", 11 to "NOV", 12 to "DIC"
    )

    private val currentReport = reports.find { it.month == monthAbbr[now.monthValue] }
    private val previousReport = reports.find {
        val prevMonth = if (now.monthValue == 1) 12 else now.monthValue - 1
        it.month == monthAbbr[prevMonth]
    }

    private val comparison = computeComparison(currentReport, previousReport)

    val currentMonthLabel: String = now.format(
        DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("es-AR"))
    )
    val monthComparisonText: String = comparison.text
    val isSpendingDown: Boolean = comparison.isDown

    var userName by mutableStateOf("Juan")
        private set

    var totalSpent by mutableStateOf(
        currentReport?.let { "$${formatMonto(it.amount.toDouble())}" } ?: "$0"
    )
        private set

    var budgetPercentage by mutableFloatStateOf(
        currentReport?.let { (it.amount / 60000f).coerceIn(0f, 1f) } ?: 0f
    )
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

    private data class MonthComparison(val text: String, val isDown: Boolean)

    private fun computeComparison(current: MonthlyReport?, previous: MonthlyReport?): MonthComparison {
        if (current != null && previous != null && previous.amount > 0f) {
            val diff = ((current.amount - previous.amount) / previous.amount * 100).roundToInt()
            return MonthComparison(
                text = "${abs(diff)}% ${if (diff <= 0) "menos" else "más"} que el mes pasado",
                isDown = diff <= 0
            )
        }
        return MonthComparison(text = "Sin datos del mes anterior", isDown = true)
    }

    private fun formatMonto(amount: Double): String =
        String.format(Locale.US, "%,.0f", amount).replace(",", ".")
}