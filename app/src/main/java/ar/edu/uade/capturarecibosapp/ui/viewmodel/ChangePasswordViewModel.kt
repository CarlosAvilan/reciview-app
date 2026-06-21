package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.domain.usecase.ChangePasswordUseCase
import ar.edu.uade.capturarecibosapp.events.ProfileNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    private val repository = DependencyProvider.provideAuthRepository()
    private val changePasswordUseCase = ChangePasswordUseCase(repository)

    private val _navigationEvents = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var contraseniaAnterior by mutableStateOf("")
    var nuevaContrasenia by mutableStateOf("")
    var nuevaContraseniaRepetida by mutableStateOf("")

    var oldPasswordError by mutableStateOf(false)
    var newPasswordError by mutableStateOf(false)
    var confirmPasswordError by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

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
        errorMessage = null
        oldPasswordError = false
        newPasswordError = false
        confirmPasswordError = false

        viewModelScope.launch {
            when (val result = changePasswordUseCase(contraseniaAnterior, nuevaContrasenia, nuevaContraseniaRepetida)) {
                is ChangePasswordUseCase.Result.Success -> {
                    _navigationEvents.emit(ProfileNavigationEvent.NavigateToBudgetSuccess) // Usando uno existente o genérico de éxito
                }
                is ChangePasswordUseCase.Result.ValidationError -> {
                    oldPasswordError = result.oldPasswordError
                    newPasswordError = result.newPasswordError
                    confirmPasswordError = result.confirmPasswordError
                    errorMessage = result.message
                }
                is ChangePasswordUseCase.Result.Failure -> {
                    errorMessage = result.message
                }
            }
        }
    }
}
