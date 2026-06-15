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
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val expenseRepository = DependencyProvider.provideExpenseRepository(application)
    private val userId = SessionManager.userId ?: ""

    var nameError by mutableStateOf(false)
        private set
    var budgetError by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Lista cruda de Room para búsquedas síncronas rápidas (compatibilidad con UI actual)
    private val _rawCategories = MutableStateFlow<List<UserCategory>>(emptyList())

    // Observamos las categorías desde el repositorio y las mapeamos a la UI inyectando el gasto real
    val categories: StateFlow<List<CategoryItem>> = repository.getCategories(userId)
        .onEach { _rawCategories.value = it }
        .flatMapLatest { list ->
            if (list.isEmpty()) return@flatMapLatest flowOf(emptyList<CategoryItem>())
            
            val flows = list.map { category ->
                expenseRepository.getTotalSpentByCategory(userId, category.name)
                    .map { spent ->
                        CategoryItem(
                            icon = category.icon,
                            name = category.name,
                            spent = spent,
                            budget = category.budget
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
        if (name == "new" || name == null) return null
        return _rawCategories.value.find { it.name == name }
    }

    /**
     * Guarda o actualiza una categoría.
     */
    fun saveCategory(nombre: String, limite: String, icon: String, existingCategory: UserCategory? = null, onResult: (Boolean) -> Unit) {
        errorMessage = null
        nameError = false
        budgetError = false

        if (nombre.isBlank()) {
            nameError = true
            errorMessage = "El nombre no puede estar vacío"
            onResult(false)
            return
        }

        val budget = try {
            val cleanLimite = limite.replace("$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
            cleanLimite.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }

        if (budget == null) {
            budgetError = true
            errorMessage = "Ingresa un monto válido"
            onResult(false)
            return
        }

        if (budget < 0) {
            budgetError = true
            errorMessage = "El presupuesto no puede ser negativo"
            onResult(false)
            return
        }

        val category = existingCategory?.copy(
            name = nombre,
            icon = icon,
            budget = budget
        ) ?: UserCategory(
            name = nombre,
            icon = icon,
            budget = budget,
            userId = userId
        )

        viewModelScope.launch {
            val result = repository.saveCategory(category)
            if (result.isSuccess) {
                onResult(true)
            } else {
                errorMessage = "Error al guardar la categoría"
                onResult(false)
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
