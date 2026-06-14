package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class PersonalInfoViewModel(
    private val userRepository: UserRepository = DependencyProvider.provideUserRepository()
) : ViewModel() {
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
                email = profile.email ?: ""
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

    fun guardarCambios(onSuccess: () -> Unit) {
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
                onSuccess()
            } else {
                errorMessage = "Error al actualizar perfil"
            }
        }
    }

    fun eliminarCuenta(onSuccess: () -> Unit) {
        // Lógica para eliminar cuenta si se desea implementar
        onSuccess()
    }
}
