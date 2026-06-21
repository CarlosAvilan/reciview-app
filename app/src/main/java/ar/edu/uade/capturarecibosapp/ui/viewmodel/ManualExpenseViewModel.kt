package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus
import ar.edu.uade.capturarecibosapp.events.ManualExpenseNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ManualExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val ticketRepository = DependencyProvider.provideTicketRepository(application)
    private val userId = SessionManager.userId ?: ""

    private val uiFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _navigationEvents = MutableSharedFlow<ManualExpenseNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var monto by mutableStateOf("0.00")
    var establecimiento by mutableStateOf("")
    var categoria by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var fecha by mutableStateOf(LocalDate.now().format(uiFormatter))
    var photoUrl by mutableStateOf("")

    // UI states para errores
    var montoError by mutableStateOf(false)
    var establecimientoError by mutableStateOf(false)
    var categoriaError by mutableStateOf(false)

    val categories: StateFlow<List<UserCategory>> = categoryRepository.getCategories(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onMontoChange(newValue: String) {
        // Permitir solo números y un punto decimal
        if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
            monto = newValue
            montoError = false
        }
    }

    fun onEstablecimientoChange(newValue: String) {
        establecimiento = newValue
        establecimientoError = false
    }

    fun onCategoriaChange(newValue: String) {
        categoria = newValue
        categoriaError = false
    }

    fun onDescripcionChange(newValue: String) {
        descripcion = newValue
    }

    fun onFechaChange(newValue: String) {
        fecha = newValue
    }

    fun initializeWithTicket(ticket: Ticket) {
        monto = ticket.amount.toString()
        establecimiento = ticket.establishment
        descripcion = ticket.description
        photoUrl = ticket.photoUrl ?: ""

        // Formatear fecha de la API (yyyy-MM-dd) a la UI (dd/MM/yyyy)
        fecha = try {
            LocalDate.parse(ticket.createdAt, apiFormatter).format(uiFormatter)
        } catch (e: Exception) {
            ticket.createdAt // Fallback
        }

        // Buscamos el nombre de la categoría si ya tiene un ID asignado
        viewModelScope.launch {
            categories.collect { list ->
                if (list.isNotEmpty()) {
                    list.find { it.id == ticket.categoryId }?.let {
                        categoria = it.name
                    }
                }
            }
        }
    }

    fun guardarGasto() {
        val amount = monto.toFloatOrNull()
        
        var hasError = false
        if (amount == null || amount <= 0) {
            montoError = true
            hasError = true
        }
        if (establecimiento.isBlank()) {
            establecimientoError = true
            hasError = true
        }
        if (categoria.isBlank()) {
            categoriaError = true
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            // Buscamos el ID de la categoría seleccionada por nombre
            val selectedCat = categories.value.find { it.name == categoria }
            
            // Parseamos la fecha de la UI al formato de la API
            val fechaApi = try {
                LocalDate.parse(fecha, uiFormatter).format(apiFormatter)
            } catch (e: Exception) {
                fecha // fallback
            }

            val ticket = Ticket(
                createdAt = fechaApi,
                userId = userId,
                categoryId = selectedCat?.id,
                establishment = establecimiento,
                amount = amount ?: 0f,
                photoUrl = null, // Guardado manual sin foto
                description = descripcion,
                syncStatus = SyncStatus.PENDIENTE_AGREGAR
            )
            
            val result = ticketRepository.saveTicket(ticket)
            if (result.isSuccess) {
                _navigationEvents.emit(ManualExpenseNavigationEvent.NavigateToSuccess)
            }
        }
    }
}
