package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.events.ProfileNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    private val _navigationEvents = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var contraseniaAnterior by mutableStateOf("")
    var nuevaContrasenia by mutableStateOf("")
    var nuevaContraseniaRepetida by mutableStateOf("")

    fun onContraseniaAnteriorChange(newValue: String) {
        contraseniaAnterior = newValue
    }

    fun onNuevaContraseniaChange(newValue: String) {
        nuevaContrasenia = newValue
    }

    fun onNuevaContraseniaRepetidaChange(newValue: String) {
        nuevaContraseniaRepetida = newValue
    }

    fun cambiarContrasenia() {
        // lógica de cambio de contraseña
        viewModelScope.launch {
            _navigationEvents.emit(ProfileNavigationEvent.NavigateToBudgetSuccess) // Usamos BudgetSuccess o uno genérico de éxito
        }
    }
}
