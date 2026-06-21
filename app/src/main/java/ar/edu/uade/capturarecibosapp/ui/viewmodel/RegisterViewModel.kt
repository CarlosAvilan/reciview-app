package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val userEmail: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val authRepository = DependencyProvider.provideAuthRepository()
    private val registerUserUseCase = RegisterUserUseCase(authRepository)

    var nombreCompleto by mutableStateOf("")
    var correoElectronico by mutableStateOf("")
    var fechaNacimiento by mutableStateOf<LocalDate?>(null)
        private set
    var paisNacimiento by mutableStateOf("")
    var password by mutableStateOf("")

    // Estados de validación para la UI
    var passwordError by mutableStateOf(false)
    var emailError by mutableStateOf(false)
    var birthDateError by mutableStateOf(false)

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

    fun onFechaNacimientoChange(newValue: LocalDate) {
        fechaNacimiento = newValue
        birthDateError = false
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

    fun registrarse(onSuccess: () -> Unit) {
        passwordError = false
        emailError = false
        birthDateError = false
        uiState = RegisterState.Loading

        viewModelScope.launch {
            when (val result = registerUserUseCase(
                email = correoElectronico,
                password = password,
                name = nombreCompleto,
                birth = fechaNacimiento,
                country = paisNacimiento,
                termsAccepted = terminosAceptados
            )) {
                is RegisterUserUseCase.Result.Success -> {
                    uiState = RegisterState.Success(result.email)
                    onSuccess()
                }
                is RegisterUserUseCase.Result.PasswordError -> {
                    passwordError = true
                    uiState = RegisterState.Error(result.message)
                }
                is RegisterUserUseCase.Result.EmailError -> {
                    emailError = true
                    uiState = RegisterState.Error(result.message)
                }
                is RegisterUserUseCase.Result.BirthDateError -> {
                    birthDateError = true
                    uiState = RegisterState.Error(result.message)
                }
                is RegisterUserUseCase.Result.TermsError -> {
                    uiState = RegisterState.Error(result.message)
                }
                is RegisterUserUseCase.Result.Failure -> {
                    uiState = RegisterState.Error(result.message)
                }
            }
        }
    }
}