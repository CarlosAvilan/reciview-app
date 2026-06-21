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
import ar.edu.uade.capturarecibosapp.domain.usecase.SaveCategoryUseCase
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val expenseRepository = DependencyProvider.provideExpenseRepository(application)
    private val userId = SessionManager.userId ?: ""
    private val saveCategoryUseCase = SaveCategoryUseCase(repository)

    var nameError by mutableStateOf(false)
        private set
    var budgetError by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val _rawCategories = MutableStateFlow<List<UserCategory>>(emptyList())

    val categories: StateFlow<List<CategoryItem>> = repository.getCategories(userId)
        .onEach { _rawCategories.value = it }
        .flatMapLatest { list ->
            if (list.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val flows = list.map { category ->
                expenseRepository.getTotalSpentByCategory(userId, category.name)
                    .map { spent ->
                        CategoryItem(
                            icon = category.icon,
                            name = category.name,
                            spent = spent,
                            budget = category.budget,
                        )
                    }
            }
            combine(flows) { it.toList() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        sync()
    }

    fun sync() {
        viewModelScope.launch {
            repository.syncPendingCategories()
        }
    }

    fun getCategoryByName(name: String?): UserCategory? {
        if ((name == "new") || (name == null)) return null
        return _rawCategories.value.find { it.name == name }
    }

    /**
     * El ViewModel ya NO valida ni parsea moneda. Solo:
     * 1) limpia estado de error previo
     * 2) delega la regla de negocio al Use Case
     * 3) traduce el resultado a estado de UI
     */
    fun saveCategory(
        nombre: String,
        limite: String,
        icon: String,
        existingCategory: UserCategory? = null,
        onResult: (Boolean) -> Unit
    ) {
        errorMessage = null
        nameError = false
        budgetError = false

        viewModelScope.launch {
            when (val result = saveCategoryUseCase(nombre, limite, icon, userId, existingCategory)) {
                is SaveCategoryUseCase.Result.Success -> {
                    onResult(true)
                }
                is SaveCategoryUseCase.Result.ValidationError -> {
                    nameError = result.nameError
                    budgetError = result.budgetError
                    errorMessage = result.message
                    onResult(false)
                }
                is SaveCategoryUseCase.Result.Failure -> {
                    errorMessage = result.message
                    onResult(false)
                }
            }
        }
    }

    fun deleteCategory(category: UserCategory) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    private fun UserCategory.toItem() = CategoryItem(
        icon = icon,
        name = name,
        spent = 0.0,
        budget = budget
    )
}
