package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.repository.UserRepository
import ar.edu.uade.capturarecibosapp.domain.usecase.UpdatePersonalInfoUseCase
import ar.edu.uade.capturarecibosapp.events.ProfileNavigationEvent
import ar.edu.uade.capturarecibosapp.ui.components.LoadingState
import ar.edu.uade.capturarecibosapp.utils.getInitials
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PersonalInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository = DependencyProvider.provideUserRepository(application)
    private val updatePersonalInfoUseCase = UpdatePersonalInfoUseCase(userRepository)

    private val _navigationEvents = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var nombre by mutableStateOf("")
    val iniciales by derivedStateOf { getInitials(nombre) }
    var email by mutableStateOf("")
    var telefono by mutableStateOf("")
    var fechaNacimiento by mutableStateOf<LocalDate?>(null)
        private set
    var paisResidencia by mutableStateOf("")
    var loadingState by mutableStateOf(LoadingState.NONE)

    var errorMessage by mutableStateOf<String?>(null)
    var nameError by mutableStateOf<String?>(null)
        private set
    var phoneError by mutableStateOf<String?>(null)
        private set
    var birthDateError by mutableStateOf<String?>(null)
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val id = SessionManager.userId
        if (id == null) {
            errorMessage = "Inicia sesión para ver tus datos"
            return
        }

        loadingState = LoadingState.LOADING_PROFILE

        viewModelScope.launch {
            val result = userRepository.getProfile()
            loadingState = LoadingState.NONE

            result.onSuccess { profile ->
                nombre = profile.name
                email = profile.email?.takeIf { it.isNotBlank() } ?: (SessionManager.userEmail ?: "")
                telefono = profile.phone ?: ""
                fechaNacimiento = profile.birth.takeIf { it.isNotBlank() }?.let {
                    try { LocalDate.parse(it, apiDateFormatter) } catch (e: Exception) { null }
                }
                paisResidencia = profile.country ?: ""
            }.onFailure {
                errorMessage = "No se pudo cargar la información"
                email = SessionManager.userEmail ?: ""
            }
        }
    }

    fun onNombreChange(newValue: String) {
        nombre = newValue
        nameError = null
    }

    fun onEmailChange(newValue: String) { email = newValue }

    fun onTelefonoChange(newValue: String) {
        telefono = newValue
        phoneError = null
    }

    fun onFechaNacimientoChange(newValue: LocalDate) {
        fechaNacimiento = newValue
        birthDateError = null
    }

    fun onPaisChange(newValue: String) { paisResidencia = newValue }

    fun guardarCambios() {
        nameError = null
        phoneError = null
        birthDateError = null
        loadingState = LoadingState.SAVING_CHANGES
        errorMessage = null

        viewModelScope.launch {
            when (val result = updatePersonalInfoUseCase(
                nombre = nombre,
                fechaNacimiento = fechaNacimiento,
                paisResidencia = paisResidencia,
                telefono = telefono
            )) {
                is UpdatePersonalInfoUseCase.Result.ValidationError -> {
                    loadingState = LoadingState.NONE
                    nameError = result.nameError
                    phoneError = result.phoneError
                    birthDateError = result.birthDateError
                }
                is UpdatePersonalInfoUseCase.Result.Success -> {
                    loadingState = LoadingState.NONE
                    _navigationEvents.emit(ProfileNavigationEvent.NavigateToProfile)
                }
                is UpdatePersonalInfoUseCase.Result.Failure -> {
                    loadingState = LoadingState.NONE
                    errorMessage = result.message
                }
            }
        }
    }

    fun eliminarCuenta() {
        loadingState = LoadingState.DELETING_ACCOUNT
        errorMessage = null

        viewModelScope.launch {
            val result = userRepository.deleteAccount()
            loadingState = LoadingState.NONE

            if (result.isSuccess) _navigationEvents.emit(ProfileNavigationEvent.NavigateToLogin)
            else errorMessage = "No se pudo eliminar la cuenta. Por favor, intenta de nuevo más tarde."
        }
    }
}
