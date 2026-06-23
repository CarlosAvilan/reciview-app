package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.domain.usecase.ChangePasswordUseCase
import ar.edu.uade.capturarecibosapp.events.ProfileNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    private val changePasswordUseCase = ChangePasswordUseCase()

    private val _navigationEvents = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var contraseniaAnterior by mutableStateOf("")
    var nuevaContrasenia by mutableStateOf("")
    var nuevaContraseniaRepetida by mutableStateOf("")

    var contraseniaAnteriorError by mutableStateOf<String?>(null)
        private set
    var nuevaContraseniaError by mutableStateOf<String?>(null)
        private set
    var nuevaContraseniaRepetidaError by mutableStateOf<String?>(null)
        private set

    fun onContraseniaAnteriorChange(newValue: String) {
        contraseniaAnterior = newValue
        contraseniaAnteriorError = null
    }

    fun onNuevaContraseniaChange(newValue: String) {
        nuevaContrasenia = newValue
        nuevaContraseniaError = null
        nuevaContraseniaRepetidaError = null
    }

    fun onNuevaContraseniaRepetidaChange(newValue: String) {
        nuevaContraseniaRepetida = newValue
        nuevaContraseniaRepetidaError = null
    }

    fun cambiarContrasenia() {
        contraseniaAnteriorError = null
        nuevaContraseniaError = null
        nuevaContraseniaRepetidaError = null

        when (val result = changePasswordUseCase(
            currentPassword = contraseniaAnterior,
            newPassword = nuevaContrasenia,
            repeatPassword = nuevaContraseniaRepetida
        )) {
            is ChangePasswordUseCase.Result.ValidationError -> {
                contraseniaAnteriorError = result.currentPasswordError
                nuevaContraseniaError = result.newPasswordError
                nuevaContraseniaRepetidaError = result.repeatPasswordError
            }
            is ChangePasswordUseCase.Result.Success -> {
                viewModelScope.launch {
                    _navigationEvents.emit(ProfileNavigationEvent.NavigateToBudgetSuccess)
                }
            }
        }
    }
}
