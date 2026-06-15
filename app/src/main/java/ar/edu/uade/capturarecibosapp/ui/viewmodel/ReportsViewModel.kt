package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.data.local.seeders.ExpenseSeeder
import ar.edu.uade.capturarecibosapp.data.local.seeders.TicketSeeder
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import ar.edu.uade.capturarecibosapp.domain.ReportCalculator

class ReportsViewModel : ViewModel() {

    private val calculatedReports = ReportCalculator.calcular(
        tickets = TicketSeeder().provideInitialTickets(),
        expenses = ExpenseSeeder().provideInitialExpenses()
    )

    var monthlyEvolution by mutableStateOf(calculatedReports)
        private set

    var selectedReport by mutableStateOf(
        calculatedReports.lastOrNull() ?: MonthlyReport(
            month = "-", amount = 0f, averageCost = "$0", mostActiveDay = "-"
        )
    )
        private set

    fun onReportSelected(report: MonthlyReport) {
        selectedReport = report
    }
}
