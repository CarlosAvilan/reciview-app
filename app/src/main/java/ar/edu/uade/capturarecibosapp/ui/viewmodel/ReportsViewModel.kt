package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport

class ReportsViewModel : ViewModel() {
    var monthlyEvolution by mutableStateOf(
        listOf(
            MonthlyReport("ENE", 1500f, "$1.200", "Lunes"),
            MonthlyReport("FEB", 2800f, "$1.350", "Miércoles"),
            MonthlyReport("MAR", 3500f, "$1.400", "Viernes"),
            MonthlyReport("ABR", 4200f, "$1.380", "Jueves"),
            MonthlyReport("MAY", 5500f, "$1.450", "Sábado")
        )
    )
        private set

    var selectedReport by mutableStateOf(monthlyEvolution.last())
        private set

    fun onReportSelected(report: MonthlyReport) {
        selectedReport = report
    }
}
