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
import ar.edu.uade.capturarecibosapp.events.ProfileNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository = DependencyProvider.provideUserRepository(application)
    
    private val _navigationEvents = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var nombre by mutableStateOf("Cargando...")
    var email by mutableStateOf("")
    var presupuestoMensual by mutableStateOf("0.00")
    var notificacionesEnabled by mutableStateOf(true)

    // Estado para la edición del presupuesto
    var budgetInput by mutableStateOf("")
    var budgetError by mutableStateOf<String?>(null)

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
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userRepository.getLocalPreferences().collect { prefs ->
                prefs?.let {
                    presupuestoMensual = String.format(Locale.US, "%.2f", it.monthlyMax)
                    // Inicializar el input con el valor actual si está vacío (primera carga)
                    if (budgetInput.isEmpty()) {
                        budgetInput = if (it.monthlyMax > 0) presupuestoMensual else ""
                    }
                    notificacionesEnabled = it.notificationsOn
                }
            }
        }
    }

    fun onBudgetInputChange(newValue: String) {
        // Solo permitir números y un punto decimal
        if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
            budgetInput = newValue
            budgetError = null
        }
    }

    fun loadUserProfile() {
        val id = SessionManager.userId
        if (id == null) {
            nombre = "No identificado"
            return
        }

        viewModelScope.launch {
            // Cargar perfil básico
            val result = userRepository.getProfile()
            result.onSuccess { profile ->
                nombre = profile.name
                email = profile.email ?: SessionManager.userEmail ?: ""
            }.onFailure {
                nombre = "Error al cargar"
                email = SessionManager.userEmail ?: ""
            }

            // Sincronizar preferencias de Supabase
            userRepository.fetchAndCachePreferences()
        }
    }

    fun updateBudget() {
        val amount = budgetInput.toFloatOrNull()
        if (amount == null || amount <= 0) {
            budgetError = "Ingrese un monto válido mayor a 0"
            return
        }

        viewModelScope.launch {
            // En Offline-First, el éxito local es suficiente para proceder en la UI
            // La sincronización ocurre en segundo plano dentro del repositorio
            val result = userRepository.updateBudget(budgetInput)
            
            result.onSuccess {
                _navigationEvents.emit(ProfileNavigationEvent.NavigateToBudgetSuccess)
            }.onFailure {
                // Si falla la sincronización, igual ya se guardó en Room (Offline-First)
                // Pero logueamos el error para depuración
                android.util.Log.e("ProfileViewModel", "Error sincronizando presupuesto: ${it.message}")
                _navigationEvents.emit(ProfileNavigationEvent.NavigateToBudgetSuccess)
            }
        }
    }

    fun onNotificacionesToggle(enabled: Boolean) {
        viewModelScope.launch {
            val result = userRepository.updateNotifications(enabled)
            if (result.isSuccess) {
                notificacionesEnabled = enabled
            }
        }
    }

    fun cerrarSesion() {
        SessionManager.clear()
        viewModelScope.launch {
            _navigationEvents.emit(ProfileNavigationEvent.NavigateToLogin)
        }
    }
}
