package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    var nombre by mutableStateOf("Juan Pérez")
    var email by mutableStateOf("juan.perez@email.com")
    var presupuestoMensual by mutableStateOf("60.000,00")
    var moneda by mutableStateOf("ARS ($)")
    var notificacionesEnabled by mutableStateOf(true)

    fun onNotificacionesToggle(enabled: Boolean) {
        notificacionesEnabled = enabled
    }

    fun cerrarSesion(onSuccess: () -> Unit) {
        onSuccess()
    }
}
