package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.events.ManualExpenseNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import ar.edu.uade.capturarecibosapp.domain.usecase.SaveManualExpenseUseCase
import ar.edu.uade.capturarecibosapp.utils.ImageStorage
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
    private val saveManualExpenseUseCase = SaveManualExpenseUseCase(ticketRepository)
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
    
    // Almacenamos el bitmap temporalmente si viene de un escaneo
    var bitmapToSave by mutableStateOf<Bitmap?>(null)

    var montoError by mutableStateOf<String?>(null)
        private set
    var establecimientoError by mutableStateOf<String?>(null)
        private set
    var categoriaError by mutableStateOf<String?>(null)
        private set

    // Estado de carga y error general (falla de red/guardado, no de validación)
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    val categories: StateFlow<List<UserCategory>> = categoryRepository.getCategories(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            try {
                categoryRepository.syncPendingCategories()
            } catch (e: Exception) { Log.e("ManualExpenseViewModel", "Error al sincronizar categorías", e) }
        }
    }

    fun onMontoChange(newValue: String) {
        // Permitir solo números y un punto decimal
        if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
            monto = newValue
            montoError = null
            errorMessage = null
        }
    }

    fun onEstablecimientoChange(newValue: String) {
        establecimiento = newValue
        establecimientoError = null
        errorMessage = null
    }

    fun onCategoriaChange(newValue: String) {
        categoria = newValue
        categoriaError = null
        errorMessage = null
    }

    fun onDescripcionChange(newValue: String) {
        descripcion = newValue
    }

    fun onFechaChange(newValue: String) {
        fecha = newValue
    }

    fun initializeWithTicket(ticket: Ticket, bitmap: Bitmap? = null) {
        monto = ticket.amount.toString()
        establecimiento = ticket.establishment
        descripcion = ticket.description
        photoUrl = ticket.photoUrl ?: ""
        bitmapToSave = bitmap

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
        montoError = null
        establecimientoError = null
        categoriaError = null
        errorMessage = null
        isLoading = true

        viewModelScope.launch {
            // Si hay un bitmap, lo guardamos localmente primero
            val localPhotoPath = bitmapToSave?.let {
                ImageStorage.saveInternalImage(getApplication(), it)
            } ?: if (photoUrl.isNotEmpty()) photoUrl else null

            when (val result = saveManualExpenseUseCase(
                montoRaw = monto,
                establecimiento = establecimiento,
                categoriaNombre = categoria,
                descripcion = descripcion,
                fechaUi = fecha,
                userId = userId,
                categories = categories.value,
                photoUrl = localPhotoPath
            )) {
                is SaveManualExpenseUseCase.Result.Success -> {
                    isLoading = false
                    _navigationEvents.emit(ManualExpenseNavigationEvent.NavigateToSuccess)
                }
                is SaveManualExpenseUseCase.Result.ValidationError -> {
                    isLoading = false
                    montoError = result.montoError
                    establecimientoError = result.establecimientoError
                    categoriaError = result.categoriaError
                }
                is SaveManualExpenseUseCase.Result.Failure -> {
                    isLoading = false
                    errorMessage = result.message
                }
            }
        }
    }
}
