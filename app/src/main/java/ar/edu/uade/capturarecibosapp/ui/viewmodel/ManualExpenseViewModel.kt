package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ManualExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val expenseRepository = DependencyProvider.provideExpenseRepository(application)
    private val userId = SessionManager.userId ?: ""

    var monto by mutableStateOf("0.00")
    var establecimiento by mutableStateOf("")
    var categoria by mutableStateOf("")
    var fecha by mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
    
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

    fun onFechaChange(newValue: String) {
        fecha = newValue
    }

    fun guardarGasto(onSuccess: () -> Unit) {
        val amount = monto.toDoubleOrNull()
        
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
            val expense = ExpenseItem(
                photoUrl = 0, // Icono por defecto o según lógica
                userId = userId,
                title = establecimiento,
                date = fecha,
                category = categoria,
                amount = amount ?: 0.0
            )
            expenseRepository.saveExpense(expense)
            onSuccess()
        }
    }
}
