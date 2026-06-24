package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.domain.usecase.RegisterUserUseCase
import ar.edu.uade.capturarecibosapp.events.AuthNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val userEmail: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val authRepository = DependencyProvider.provideAuthRepository()
    private val registerUserUseCase = RegisterUserUseCase(authRepository)

    private val _navigationEvents = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var nombreCompleto by mutableStateOf("")
    var correoElectronico by mutableStateOf("")
    var fechaNacimiento by mutableStateOf<LocalDate?>(null)
        private set
    var paisNacimiento by mutableStateOf("")
    var countrySearchText by mutableStateOf("")
    var isCountrySheetVisible by mutableStateOf(false)
    var password by mutableStateOf("")

    var nameError by mutableStateOf(false)
    var emailError by mutableStateOf(false)
    var passwordError by mutableStateOf(false)
    var countryError by mutableStateOf(false)
    var birthDateError by mutableStateOf(false)

    var terminosAceptados by mutableStateOf(false)
    var permisosCamaraAceptados by mutableStateOf(false)
    var haLeidoTerminos by mutableStateOf(false)

    var uiState by mutableStateOf<RegisterState>(RegisterState.Idle)
        private set

    fun onNombreChange(newValue: String) {
        nombreCompleto = newValue
        nameError = false
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
        countryError = false
        isCountrySheetVisible = false
        countrySearchText = ""
    }

    fun onCountrySearchChange(newValue: String) {
        countrySearchText = newValue
    }

    fun toggleCountrySheet(visible: Boolean) {
        isCountrySheetVisible = visible
        if (!visible) countrySearchText = ""
    }

    val filteredCountries: List<String>
        get() {
            val allCountries = java.util.Locale.getISOCountries().map { code ->
                java.util.Locale("", code).displayCountry
            }.sorted()
            
            return if (countrySearchText.isEmpty()) {
                allCountries
            } else {
                allCountries.filter { 
                    it.contains(countrySearchText, ignoreCase = true) 
                }
            }
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

    fun registrarse() {
        nameError = false
        emailError = false
        passwordError = false
        countryError = false
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
                    _navigationEvents.emit(AuthNavigationEvent.NavigateToRegisterSuccess)
                }
                is RegisterUserUseCase.Result.NameError -> {
                    nameError = true
                    uiState = RegisterState.Error(result.message)
                }
                is RegisterUserUseCase.Result.EmailError -> {
                    emailError = true
                    uiState = RegisterState.Error(result.message)
                }
                is RegisterUserUseCase.Result.PasswordError -> {
                    passwordError = true
                    uiState = RegisterState.Error(result.message)
                }
                is RegisterUserUseCase.Result.CountryError -> {
                    countryError = true
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
