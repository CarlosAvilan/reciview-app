package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.local.SharedPreferencesManager
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private val authRepository = DependencyProvider.provideAuthRepository()

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

    fun login(context: Context, onSuccess: () -> Unit) {
        if (correoElectronico.isNotEmpty() && contrasenia.isNotEmpty()) {
            isLoading = true
            errorMessage = null
            viewModelScope.launch {
                val result = authRepository.login(correoElectronico, contrasenia)
                isLoading = false
                if (result.isSuccess) {
                    // Guardamos el ID en SharedPreferences
                    val sharedPreferencesManager = SharedPreferencesManager(context)
                    // Usamos un ID de prueba
                    sharedPreferencesManager.saveUserId("user123")

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
