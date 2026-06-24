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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ar.edu.uade.capturarecibosapp.data.local.SharedPreferencesManager
import ar.edu.uade.capturarecibosapp.utils.getInitials
import java.util.Locale

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository = DependencyProvider.provideUserRepository(application)
    
    private val _navigationEvents = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var telefono by mutableStateOf("")
    var fechaNacimiento by mutableStateOf("")
    var pais by mutableStateOf("")
    
    var presupuestoMensual by mutableStateOf("0.00")
    var notificacionesEnabled by mutableStateOf(true)

    // Estado para la edición del presupuesto
    var budgetInput by mutableStateOf("")
    var budgetError by mutableStateOf<String?>(null)

    // Iniciales dinámicas basadas en el nombre real
    val iniciales by derivedStateOf { getInitials(nombre) }

    init {
        observeProfile()
        observePreferences()
        loadData()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().collectLatest { user ->
                user?.let {
                    nombre = it.name
                    email = it.email
                    telefono = it.phone ?: ""
                    fechaNacimiento = it.birth
                    pais = it.country ?: ""
                }
            }
        }
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

    private fun loadData() {
        if (SessionManager.userId == null) {
            nombre = "No identificado"
            return
        }
        viewModelScope.launch {
            userRepository.fetchAndCacheProfile()
            userRepository.fetchAndCachePreferences()
        }
    }

    @Deprecated("Usar observeProfile y loadData", ReplaceWith("loadData()"))
    fun loadUserProfile() {
        loadData()
    }

    fun onBudgetInputChange(newValue: String) {
        // Solo permitir números y un punto decimal
        if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
            budgetInput = newValue
            budgetError = null
        }
    }

    fun updateBudget() {
        val amount = budgetInput.toFloatOrNull()
        if (amount == null || amount <= 0) {
            budgetError = "Ingrese un monto válido mayor a 0"
            return
        }

        viewModelScope.launch {
            val result = userRepository.updateBudget(budgetInput)
            
            result.onSuccess {
                _navigationEvents.emit(ProfileNavigationEvent.NavigateToBudgetSuccess)
            }.onFailure {
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
        SharedPreferencesManager(getApplication()).clearSession()
        viewModelScope.launch {
            _navigationEvents.emit(ProfileNavigationEvent.NavigateToLogin)
        }
    }
}
