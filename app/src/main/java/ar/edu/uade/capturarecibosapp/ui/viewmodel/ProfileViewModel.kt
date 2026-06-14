package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository = DependencyProvider.provideUserRepository()
) : ViewModel() {
    var nombre by mutableStateOf("Cargando...")
    var email by mutableStateOf("")
    var presupuestoMensual by mutableStateOf("0,00")
    var notificacionesEnabled by mutableStateOf(true)

    // Iniciales dinámicas basadas en el nombre real
    val iniciales by derivedStateOf {
        if (nombre.isEmpty() || nombre == "Cargando..." || nombre == "Error al cargar") {
            "?"
        } else {
            val words = nombre.trim().split(" ")
            if (words.size >= 2) {
                "${words[0].first()}${words[1].first()}".uppercase()
            } else if (words.isNotEmpty()) {
                words[0].first().toString().uppercase()
            } else {
                "?"
            }
        }
    }

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val id = SessionManager.userId
        if (id == null) {
            nombre = "No identificado"
            return
        }

        viewModelScope.launch {
            val result = userRepository.getProfile()
            result.onSuccess { profile ->
                nombre = profile.name
                // Usamos el email del perfil si existe, sino el de la sesión
                email = profile.email ?: SessionManager.userEmail ?: ""
            }.onFailure {
                nombre = "Error al cargar"
                email = SessionManager.userEmail ?: ""
            }
        }
    }

    fun updateBudget(newBudget: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = userRepository.updateBudget(newBudget)
            if (result.isSuccess) {
                presupuestoMensual = newBudget
                onSuccess()
            }
        }
    }

    fun onNotificacionesToggle(enabled: Boolean) {
        notificacionesEnabled = enabled
    }

    fun cerrarSesion(onSuccess: () -> Unit) {
        SessionManager.clear()
        onSuccess()
    }
}
