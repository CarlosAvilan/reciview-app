package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.local.SharedPreferencesManager
import ar.edu.uade.capturarecibosapp.events.AuthNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private val authRepository = DependencyProvider.provideAuthRepository()
    private val _navigationEvents = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

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

    fun login(context: Context) {
        val userRepository = DependencyProvider.provideUserRepository(context)

        if (correoElectronico.isNotEmpty() && contrasenia.isNotEmpty()) {
            isLoading = true
            errorMessage = null
            viewModelScope.launch {
                try {
                    val loginResult = authRepository.login(
                        correoElectronico,
                        contrasenia
                    )
                    if (loginResult.isFailure) {
                        errorMessage = loginResult.exceptionOrNull()?.message
                            ?: "Credenciales inválidas"
                        return@launch
                    }

                    val profileResult = userRepository.getProfile()
                    if (profileResult.isFailure) {
                        errorMessage = profileResult.exceptionOrNull()?.message
                            ?: "No se pudo obtener el perfil"
                        return@launch
                    }

                    val profile = profileResult.getOrNull()
                    if (profile?.deleted == true) {
                        errorMessage = "La cuenta ha sido eliminada"
                        return@launch
                    }

                    //Exito
                    val sharedPreferencesManager =
                        SharedPreferencesManager(context)
                    profile?.userId?.let {
                        sharedPreferencesManager.saveUserId(it)
                    }

                    _navigationEvents.emit(AuthNavigationEvent.NavigateToHome)
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Error inesperado"
                } finally {
                    isLoading = false
                }
            }

        } else {
            errorMessage = "Por favor, completa todos los campos"
        }
    }
}
