package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    var correoElectronico by mutableStateOf("")
    var contrasenia by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onCorreoElectronicoChange(newValue: String) {
        correoElectronico = newValue
    }

    fun onContraseniaChange(newValue: String) {
        contrasenia = newValue
    }

    fun login(onSuccess: () -> Unit) {
        if (correoElectronico.isNotEmpty() && contrasenia.isNotEmpty()) {
            isLoading = true
            errorMessage = null
            viewModelScope.launch {
                val result = authRepository.login(correoElectronico, contrasenia)
                isLoading = false
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                }
            }
        } else {
            errorMessage = "Por favor, completa todos los campos"
        }
    }
}
