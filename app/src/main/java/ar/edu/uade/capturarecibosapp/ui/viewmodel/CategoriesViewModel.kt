package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.repository.CategoryRepository
import ar.edu.uade.capturarecibosapp.events.CategoryNavigationEvent
import ar.edu.uade.capturarecibosapp.domain.usecase.SaveCategoryUseCase
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val ticketRepository = DependencyProvider.provideTicketRepository(application)
    private val userId = SessionManager.userId ?: ""
    private val saveCategoryUseCase = SaveCategoryUseCase(repository)

    private val _navigationEvents = MutableSharedFlow<CategoryNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    var nameError by mutableStateOf(false)
        private set
    var budgetError by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Lista cruda de Room para búsquedas síncronas rápidas (compatibilidad con UI actual)
    private val _rawCategories = MutableStateFlow<List<UserCategory>>(emptyList())

    // Combina categorías con tickets del mes actual para calcular el gasto real por categoría
    val categories: StateFlow<List<CategoryItem>> = combine(
        repository.getCategories(userId).onEach { _rawCategories.value = it },
        ticketRepository.getTickets(userId)
    ) { categoryList, allTickets ->
        val now = java.time.LocalDate.now()
        val currentMonthTickets = allTickets.filter {
            try {
                val d = java.time.LocalDate.parse(it.createdAt.substringBefore('T'))
                d.year == now.year && d.monthValue == now.monthValue
            } catch (e: Exception) { false }
        }
        categoryList.map { category ->
            val spent = currentMonthTickets
                .filter { it.categoryId == category.id }
                .sumOf { it.amount.toDouble() }
            CategoryItem(
                icon = category.icon,
                name = category.name,
                spent = spent,
                budget = category.budget
            )
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Al iniciar, intentamos sincronizar cambios pendientes
        sync()
    }

    fun sync() {
        viewModelScope.launch {
            repository.syncPendingCategories()
        }
    }

    /**
     * Busca una categoría por su nombre.
     */
    fun getCategoryByName(name: String?): UserCategory? {
        if ((name == "new") || (name == null)) return null
        return _rawCategories.value.find { it.name == name }
    }

    /**
     * Guarda o actualiza una categoría.
     */
    fun saveCategory(nombre: String, limite: String, icon: String, existingCategory: UserCategory? = null) {
        errorMessage = null
        nameError = false
        budgetError = false

        viewModelScope.launch {
            when (val result = saveCategoryUseCase(nombre, limite, icon, userId, existingCategory)) {
                is SaveCategoryUseCase.Result.Success -> {
                    _navigationEvents.emit(CategoryNavigationEvent.NavigateToSuccess)
                }
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

    fun deleteCategory(category: UserCategory) {
        viewModelScope.launch {
            val result = repository.deleteCategory(category)
            if (result.isSuccess) {
                _navigationEvents.emit(CategoryNavigationEvent.NavigateToDeleteSuccess)
            }
        }
    }

    private fun UserCategory.toItem() = CategoryItem(
        icon = icon,
        name = name,
        spent = 0.0,
        budget = budget
    )
}
