package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport

class ReportsViewModel : ViewModel() {
    var monthlyEvolution by mutableStateOf(
        listOf(
            MonthlyReport(
                month = "ENE",
                amount = 1500f,
                averageCost = "$1.200",
                mostActiveDay = "Lunes"
            ),
            MonthlyReport(
                month = "FEB",
                amount = 2800f,
                averageCost = "$1.350",
                mostActiveDay = "Miércoles"
            ),
            MonthlyReport(
                month = "MAR",
                amount = 3500f,
                averageCost = "$1.400",
                mostActiveDay = "Viernes"
            ),
            MonthlyReport(
                month = "ABR",
                amount = 4200f,
                averageCost = "$1.380",
                mostActiveDay = "Jueves"
            ),
            MonthlyReport(
                month = "MAY",
                amount = 5500f,
                averageCost = "$1.450",
                mostActiveDay = "Sábado"
            )
        )
    )
        private set

    var selectedReport by mutableStateOf(monthlyEvolution.last())
        private set

    fun onReportSelected(report: MonthlyReport) {
        selectedReport = report
    }
}
