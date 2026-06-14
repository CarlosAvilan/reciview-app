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

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val userEmail: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val authRepository = DependencyProvider.provideAuthRepository()

    var nombreCompleto by mutableStateOf("")
    var correoElectronico by mutableStateOf("")
    var fechaNacimiento by mutableStateOf("")
    var paisNacimiento by mutableStateOf("")
    var password by mutableStateOf("")

    // Estados de validación para la UI
    var passwordError by mutableStateOf(false)
    var emailError by mutableStateOf(false)

    var terminosAceptados by mutableStateOf(false)
    var permisosCamaraAceptados by mutableStateOf(false)
    var haLeidoTerminos by mutableStateOf(false)

    var uiState by mutableStateOf<RegisterState>(RegisterState.Idle)
        private set

    fun onNombreChange(newValue: String) {
        nombreCompleto = newValue
    }

    fun onCorreoChange(newValue: String) {
        correoElectronico = newValue
        emailError = false
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        passwordError = false
    }

    fun onFechaNacimientoChange(newValue: String) {
        fechaNacimiento = newValue
    }

    fun onPaisChange(newValue: String) {
        paisNacimiento = newValue
    }

    fun onTerminosAceptadosChange(newValue: Boolean) {
        terminosAceptados = newValue
    }

    fun onPermisosCamaraAceptadosChange(newValue: Boolean) {
        permisosCamaraAceptados = newValue
    }

    fun marcarTerminosComoLeidos() {
        haLeidoTerminos = true
    }

    fun registrarse(context: Context, onSuccess: () -> Unit) {
        // Validaciones locales
        if (password.length < 6) {
            passwordError = true
            uiState = RegisterState.Error("Contraseña débil (mínimo 6 caracteres)")
            return
        }

        if (correoElectronico.isEmpty() || !correoElectronico.contains("@")) {
            emailError = true
            uiState = RegisterState.Error("Ingresa un correo válido")
            return
        }

        if (!terminosAceptados) {
            uiState = RegisterState.Error("Debes aceptar los términos y condiciones")
            return
        }

        uiState = RegisterState.Loading
        viewModelScope.launch {
            val result = authRepository.registerUser(
                email = correoElectronico,
                pass = password,
                name = nombreCompleto,
                birth = fechaNacimiento,
                country = paisNacimiento
            )

            result.fold(
                onSuccess = {
                    uiState = RegisterState.Success(it.email)

                    // Guardamos el ID en SharedPreferences
                    val sharedPreferencesManager = SharedPreferencesManager(context)
                    // Usamos un ID de prueba
                    sharedPreferencesManager.saveUserId("user123")

                    onSuccess()
                },
                onFailure = { error ->
                    val message = error.message ?: ""
                    if (message.contains("already exists", ignoreCase = true)) {
                        emailError = true
                        uiState = RegisterState.Error("El usuario ya existe")
                    } else {
                        uiState = RegisterState.Error(message)
                    }
                }
            )
        }
    }
}
