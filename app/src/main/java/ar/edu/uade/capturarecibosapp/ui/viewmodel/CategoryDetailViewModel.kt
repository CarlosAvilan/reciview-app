package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import android.util.Log
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
import ar.edu.uade.capturarecibosapp.events.CategoryNavigationEvent
import ar.edu.uade.capturarecibosapp.domain.usecase.SaveCategoryUseCase
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
    private val saveCategoryUseCase = SaveCategoryUseCase(categoryRepository)

    private val _navigationEvents = MutableSharedFlow<CategoryNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

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
    private var isDeleting = false

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
                            if (isDeleting) {
                                // Si estamos borrando, mantenemos el estado actual para evitar el flash de "Error"
                                _uiState.value
                            } else {
                                CategoryDetailUiState.Error("Categoría no encontrada")
                            }
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

    fun saveChanges() {
        errorMessage = null
        nameError = false
        budgetError = false

        viewModelScope.launch {
           when (val result = saveCategoryUseCase(editName, editBudget, editIcon, userId, currentCategory)) {
                is SaveCategoryUseCase.Result.Success -> _navigationEvents.emit(CategoryNavigationEvent.NavigateToSuccess)
                is SaveCategoryUseCase.Result.ValidationError -> {
                    nameError = result.nameError
                    budgetError = result.budgetError
                    errorMessage = result.message
                }
                is SaveCategoryUseCase.Result.Failure -> {
                    errorMessage = result.message
                }
            }
        }
    }

    fun deleteCategory() {
        val category = currentCategory ?: return
        isDeleting = true
        viewModelScope.launch {
            val result = categoryRepository.deleteCategory(category)
            if (result.isSuccess) {
                _navigationEvents.emit(CategoryNavigationEvent.NavigateToDeleteSuccess)
            } else {
                isDeleting = false
                _uiState.value = CategoryDetailUiState.Error("Error al eliminar la categoría")
            }
        }
    }
}
