package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    var nombre by mutableStateOf("Juan Pérez")
    var email by mutableStateOf("juan.perez@email.com")
    var presupuestoMensual by mutableStateOf("60.000,00")
    var notificacionesEnabled by mutableStateOf(true)

    fun updateBudget(newBudget: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = userRepository.updateBudget(newBudget)
            if (result.isSuccess) {
                presupuestoMensual = newBudget
                onSuccess()
            }
        }
    }

    fun onNotificacionesToggle(enabled: Boolean) {
        notificacionesEnabled = enabled
    }

    fun cerrarSesion(onSuccess: () -> Unit) {
        onSuccess()
    }
}
