package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ManualExpenseViewModel : ViewModel() {
    var monto by mutableStateOf("0.00")
    var establecimiento by mutableStateOf("")
    var categoria by mutableStateOf("")
    var fecha by mutableStateOf("Hoy, 10 de Mayo")

    fun onMontoChange(newValue: String) {
        monto = newValue
    }

    fun onEstablecimientoChange(newValue: String) {
        establecimiento = newValue
    }

    fun onCategoriaChange(newValue: String) {
        categoria = newValue
    }

    fun onFechaChange(newValue: String) {
        fecha = newValue
    }

    fun guardarGasto() {
        // Implementación futura
    }
}
