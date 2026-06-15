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
import ar.edu.uade.capturarecibosapp.data.repository.CategoryRepository
import ar.edu.uade.capturarecibosapp.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class CategoryDetailUiState {
    object Loading : CategoryDetailUiState()
    data class Success(
        val category: UserCategory,
        val expenses: List<ExpenseItem>,
        val totalSpent: Double
    ) : CategoryDetailUiState()
    data class Error(val message: String) : CategoryDetailUiState()
}

class CategoryDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryRepository: CategoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val expenseRepository: ExpenseRepository = DependencyProvider.provideExpenseRepository(application)
    private val userId = SessionManager.userId ?: ""

    // Estados de edición
    var editName by mutableStateOf("")
    var editBudget by mutableStateOf("")
    var editIcon by mutableStateOf("📁")

    // Estados de validación
    var nameError by mutableStateOf(false)
        private set
    var budgetError by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    // Filtro de fecha
    var dateFilterStart by mutableStateOf<LocalDate?>(null)
    var dateFilterEnd by mutableStateOf<LocalDate?>(null)

    private val _uiState = MutableStateFlow<CategoryDetailUiState>(CategoryDetailUiState.Loading)
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    private var currentCategory: UserCategory? = null

    fun loadCategory(categoryName: String) {
        viewModelScope.launch {
            _uiState.value = CategoryDetailUiState.Loading
            
            // Buscamos la categoría por nombre (como se usa en el repo actual)
            val categories = categoryRepository.getCategories(userId).first()
            val category = categories.find { it.name == categoryName }
            
            if (category != null) {
                currentCategory = category
                editName = category.name
                editBudget = category.budget.toString()
                editIcon = category.icon
                
                // Observamos los gastos de esta categoría
                expenseRepository.getExpensesForUser(userId)
                    .map { list -> 
                        list.filter { it.category == category.name } 
                    }
                    .collect { expenses ->
                        val filteredExpenses = filterExpenses(expenses)
                        _uiState.value = CategoryDetailUiState.Success(
                            category = category,
                            expenses = filteredExpenses,
                            totalSpent = filteredExpenses.sumOf { it.amount }
                        )
                    }
            } else {
                _uiState.value = CategoryDetailUiState.Error("Categoría no encontrada")
            }
        }
    }

    private fun filterExpenses(expenses: List<ExpenseItem>): List<ExpenseItem> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return expenses.filter { expense ->
            try {
                val expenseDate = LocalDate.parse(expense.date, formatter)
                val afterStart = dateFilterStart?.let { !expenseDate.isBefore(it) } ?: true
                val beforeEnd = dateFilterEnd?.let { !expenseDate.isAfter(it) } ?: true
                afterStart && beforeEnd
            } catch (e: Exception) {
                true // Si falla el parseo, lo mostramos igual
            }
        }
    }

    fun updateDateFilter(start: LocalDate?, end: LocalDate?) {
        dateFilterStart = start
        dateFilterEnd = end
        currentCategory?.let { loadCategory(it.name) }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        errorMessage = null
        nameError = false
        budgetError = false

        if (editName.isBlank()) {
            nameError = true
            errorMessage = "El nombre no puede estar vacío"
            return
        }

        val budget = try {
            val cleanBudget = editBudget.replace("$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
            cleanBudget.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }

        if (budget == null) {
            budgetError = true
            errorMessage = "Ingresa un monto válido"
            return
        }

        if (budget < 0) {
            budgetError = true
            errorMessage = "El presupuesto no puede ser negativo"
            return
        }

        val category = currentCategory ?: return
        val updatedCategory = category.copy(
            name = editName,
            budget = budget,
            icon = editIcon
        )

        viewModelScope.launch {
            val result = categoryRepository.saveCategory(updatedCategory)
            if (result.isSuccess) {
                onSuccess()
            } else {
                errorMessage = "Error al actualizar la categoría"
            }
        }
    }

    fun deleteCategory(onSuccess: () -> Unit) {
        val category = currentCategory ?: return
        viewModelScope.launch {
            val result = categoryRepository.deleteCategory(category)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.value = CategoryDetailUiState.Error("Error al eliminar la categoría")
            }
        }
    }
}
