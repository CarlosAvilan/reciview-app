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
import ar.edu.uade.capturarecibosapp.domain.usecase.SaveManualExpenseUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ManualExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val ticketRepository = DependencyProvider.provideTicketRepository(application)
    private val userId = SessionManager.userId ?: ""
    private val saveManualExpenseUseCase = SaveManualExpenseUseCase(ticketRepository)
    private val uiFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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

    fun guardarGasto(onSuccess: () -> Unit) {
        montoError = false
        establecimientoError = false
        categoriaError = false

        viewModelScope.launch {
            when (val result = saveManualExpenseUseCase(
                montoRaw = monto,
                establecimiento = establecimiento,
                categoriaNombre = categoria,
                descripcion = descripcion,
                fechaUi = fecha,
                userId = userId,
                categories = categories.value
            )) {
                is SaveManualExpenseUseCase.Result.Success -> onSuccess()
                is SaveManualExpenseUseCase.Result.ValidationError -> {
                    montoError = result.montoError
                    establecimientoError = result.establecimientoError
                    categoriaError = result.categoriaError
                }
                is SaveManualExpenseUseCase.Result.Failure -> {
                    // Si querés mostrar este error en UI, agregar un errorMessage al ViewModel
                }
            }
        }
    }
}
