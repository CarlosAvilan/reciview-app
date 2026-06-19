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
    private val categoryIdFlow = MutableStateFlow<Long?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun loadCategory(categoryName: String) {
        viewModelScope.launch {
            _uiState.value = CategoryDetailUiState.Loading
            
            // Primero buscamos por nombre para obtener el ID inicial si no lo tenemos
            val initialCategory = categoryRepository.getCategories(userId).first().find { it.name == categoryName }
            
            if (initialCategory == null) {
                _uiState.value = CategoryDetailUiState.Error("Categoría no encontrada")
                return@launch
            }

            categoryIdFlow.value = initialCategory.id

            categoryIdFlow.flatMapLatest { id ->
                if (id == null) flowOf(CategoryDetailUiState.Error("Categoría no encontrada"))
                else {
                    combine(
                        categoryRepository.categoryDao.getCategoryByIdFlow(id),
                        expenseRepository.getExpensesForUser(userId)
                    ) { category, allExpenses ->
                        if (category == null) {
                            CategoryDetailUiState.Error("Categoría no encontrada")
                        } else {
                            currentCategory = category
                            // Solo actualizamos los campos de edición si no están siendo manipulados
                            // O simplemente los inicializamos la primera vez
                            if (editName.isEmpty()) editName = category.name
                            if (editBudget.isEmpty()) editBudget = category.budget.toString()
                            
                            editIcon = category.icon
                            
                            val categoryExpenses = allExpenses.filter { it.category == category.name }
                            val filteredExpenses = filterExpenses(categoryExpenses)
                            CategoryDetailUiState.Success(
                                category = category,
                                expenses = filteredExpenses,
                                totalSpent = filteredExpenses.sumOf { it.amount }
                            )
                        }
                    }
                }
            }.collect { state ->
                _uiState.value = state
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
