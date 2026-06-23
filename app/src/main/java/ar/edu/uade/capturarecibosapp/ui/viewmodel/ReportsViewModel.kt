package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import ar.edu.uade.capturarecibosapp.domain.ReportCalculator
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val ticketRepository = DependencyProvider.provideTicketRepository(application)
    private val expenseRepository = DependencyProvider.provideExpenseRepository(application)
    private val userId = SessionManager.userId ?: ""

    private val emptyReport = MonthlyReport(
        month = "-", amount = 0f, averageCost = "$0", mostActiveDay = "-"
    )

    var monthlyEvolution by mutableStateOf<List<MonthlyReport>>(emptyList())
        private set

    var selectedReport by mutableStateOf(emptyReport)
        private set

    init {
        viewModelScope.launch {
            combine(
                ticketRepository.getTickets(userId),
                expenseRepository.getExpensesForUser(userId)
            ) { tickets, expenses ->
                ReportCalculator.calcular(tickets, expenses)
            }.collect { reports ->
                monthlyEvolution = reports
                // Mantener el mes seleccionado si sigue existiendo; si no, ir al último
                selectedReport = reports.find { it.month == selectedReport.month }
                    ?: reports.lastOrNull()
                    ?: emptyReport
            }
        }
    }

    fun onReportSelected(report: MonthlyReport) {
        selectedReport = report
    }
}
