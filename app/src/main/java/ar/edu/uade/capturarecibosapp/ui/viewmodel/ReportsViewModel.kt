package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class MonthlyReport(
    val month: String,
    val amount: Float
)

class ReportsViewModel : ViewModel() {
    var monthlyEvolution by mutableStateOf(
        listOf(
            MonthlyReport("ENE", 1500f),
            MonthlyReport("FEB", 2800f),
            MonthlyReport("MAR", 3500f),
            MonthlyReport("ABR", 4200f),
            MonthlyReport("MAY", 5500f)
        )
    )
        private set

    var averageCost by mutableStateOf("$1.450")
        private set

    var mostActiveDay by mutableStateOf("Sábado")
        private set
}
