package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.R
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseItem

class MyExpensesViewModel : ViewModel() {
    
    var totalSpent by mutableStateOf("$45.280,50")
        private set

    var statistics by mutableStateOf("+ 25% vs enero")
        private set

    var transactions by mutableStateOf(
        listOf(
            ExpenseItem(
                imageUrl = R.drawable.logo_carrefour,
                title = "Carrefour Market",
                date = "Hoy, 14:20",
                category = "Alimentos",
                amount = 12400.0
            ),
            ExpenseItem(
                imageUrl = R.drawable.logo_uber,
                title = "Uber Trip",
                date = "Ayer, 21:15",
                category = "Transporte",
                amount = 5500.0
            ),
            ExpenseItem(
                imageUrl = R.drawable.logo_edesur,
                title = "Edesur",
                date = "18 May, 09:30",
                category = "Servicios",
                amount = 9800.0
            )
        )
    )
        private set
        
    // Aquí podrías agregar métodos para cargar datos reales desde un repositorio
    // respetando el principio de Inversión de Dependencias.
}
