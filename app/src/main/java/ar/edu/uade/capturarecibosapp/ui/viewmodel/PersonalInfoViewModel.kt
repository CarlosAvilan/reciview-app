package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.repository.UserRepository
import ar.edu.uade.capturarecibosapp.events.ProfileNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository = DependencyProvider.provideUserRepository(application)
    
    private val _navigationEvents = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var telefono by mutableStateOf("")
    var fechaNacimiento by mutableStateOf("")
    var paisResidencia by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val id = SessionManager.userId
        if (id == null) {
            errorMessage = "Inicia sesión para ver tus datos"
            return
        }

        isLoading = true
        viewModelScope.launch {
            val result = userRepository.getProfile()
            isLoading = false
            result.onSuccess { profile ->
                nombre = profile.name
                email = profile.email?.takeIf { it.isNotBlank() } ?: (SessionManager.userEmail ?: "")
                telefono = profile.phone ?: ""
                fechaNacimiento = profile.birth
                paisResidencia = profile.country ?: ""
            }.onFailure {
                errorMessage = "No se pudo cargar la información"
                email = SessionManager.userEmail ?: ""
            }
        }
    }

    fun onNombreChange(newValue: String) { nombre = newValue }
    fun onEmailChange(newValue: String) { email = newValue }
    fun onTelefonoChange(newValue: String) { telefono = newValue }
    fun onFechaNacimientoChange(newValue: String) { fechaNacimiento = newValue }
    fun onPaisChange(newValue: String) { paisResidencia = newValue }

    fun guardarCambios() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            val result = userRepository.updateProfile(
                name = nombre,
                birth = fechaNacimiento,
                country = paisResidencia,
                phone = telefono
            )
            isLoading = false
            if (result.isSuccess) {
                _navigationEvents.emit(ProfileNavigationEvent.NavigateToProfile)
            } else {
                errorMessage = "Error al actualizar perfil"
            }
        }
    }

    fun eliminarCuenta() {
        viewModelScope.launch {
            // Lógica para eliminar cuenta
            _navigationEvents.emit(ProfileNavigationEvent.NavigateToLogin)
        }
    }
}
